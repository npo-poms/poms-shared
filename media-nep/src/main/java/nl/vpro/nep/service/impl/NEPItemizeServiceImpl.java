package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.nep.domain.*;
import nl.vpro.nep.service.NEPItemizeService;
import nl.vpro.nep.service.exception.ItemizerStatusException;
import nl.vpro.nep.service.exception.NEPException;

import static nl.vpro.poms.shared.Headers.NPO_DISPATCHED_TO;
import static org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM;

/**
 * See also <a href="https://jira.vpro.nl/browse/MSE-4435">JIRA</a>
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPItemizeService")
@Slf4j
public class NEPItemizeServiceImpl implements NEPItemizeService {

    static final ContentType JSON = ContentType.APPLICATION_JSON.withCharset(StandardCharsets.UTF_8);

    private final Supplier<String> itemizeLiveKey;
    private final String itemizeLiveUrl;


    private final Supplier<String> itemizeMidKey;
    private final String itemizeMidUrl;

    CloseableHttpClient httpClient = HttpClients.custom()
        .build();

    @Inject
    public NEPItemizeServiceImpl(
        @Value("${nep.itemizer-api.live.baseUrl}") @NonNull String itemizeLiveUrl,
        @Value("${nep.itemizer-api.live.key}") @NonNull String itemizeLiveKey,
        @Value("${nep.itemizer-api.mid.baseUrl}") @NonNull String itemizeMidUrl,
        @Value("${nep.itemizer-api.mid.key}") @NonNull String itemizeMidKey

    ) {
        this.itemizeLiveKey = new NEPItemizerV1Authenticator(itemizeLiveKey);
        this.itemizeLiveUrl = itemizeLiveUrl;
        this.itemizeMidKey = new NEPItemizerV1Authenticator(itemizeMidKey);
        this.itemizeMidUrl = itemizeMidUrl;
    }

    public NEPItemizeServiceImpl(String itemizeUrl, String itemizeKey) {
        this(itemizeUrl, itemizeKey, itemizeUrl, itemizeKey);
    }


    protected NEPItemizeServiceImpl(Properties properties) {
        this(
            properties.getProperty("nep.itemizer-api.baseUrl"),
            properties.getProperty("nep.itemizer-api.key")
        );
    }


    @PreDestroy
    public void shutdown() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    protected NEPItemizeResponse itemize(@NonNull NEPItemizeRequest request, String itemizeUrl, Supplier<String> itemizeKey) throws NEPException {
        String playerUrl = itemizeUrl + "/api/itemizer/job";
        log.info("Itemizing {} @ {}", request, playerUrl);
        HttpClientContext clientContext = HttpClientContext.create();
        String json = null;
        try {
            json = Jackson2Mapper.getLenientInstance().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new NEPException(e, e.getMessage());
        }
        StringEntity entity = new StringEntity(json, JSON);
        HttpPost httpPost = new HttpPost(playerUrl);
        authenticate(httpPost, itemizeKey);
        httpPost.addHeader(new BasicHeader(HttpHeaders.ACCEPT, JSON.toString()));
        log.debug("curl -XPOST -H'Content-Type: application/json' -H'Authorization: {}' -H'Accept: {}' {} --data '{}'", itemizeKey, JSON.toString(), itemizeUrl, json);
        httpPost.setEntity(entity);
        try (CloseableHttpResponse response = httpClient.execute(httpPost, clientContext)) {
            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            return Jackson2Mapper.getLenientInstance().readValue(response.getEntity().getContent(), NEPItemizeResponse.class);
        } catch (Exception e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public NEPItemizeResponse itemizeLive(String channel, Instant start, Instant end, Integer max_bitrate) throws NEPException {
        try {
            return itemize(
                NEPItemizeRequest.builder()
                    .identifier(channel)
                    .starttime(
                        NEPItemizeRequest.fromInstant(start)
                            .orElseThrow(IllegalArgumentException::new)
                    )
                    .endtime(
                        NEPItemizeRequest.fromInstant(end)
                            .orElseThrow(IllegalArgumentException::new)
                    )
                    .max_bitrate(max_bitrate)
                    .build(),
                itemizeLiveUrl,
                itemizeLiveKey
            );
        } catch (NEPException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public NEPItemizeResponse itemizeMid(String mid, Duration start, Duration end, Integer max_bitrate) throws NEPException {
        try {
            return itemize(
                NEPItemizeRequest.builder()
                    .starttime(NEPItemizeRequest.fromDuration(start, Duration.ZERO))
                    .endtime(NEPItemizeRequest.fromDuration(end).orElseThrow(IllegalArgumentException::new))
                    .identifier(mid).max_bitrate(max_bitrate).build(),
                itemizeMidUrl,
                itemizeMidKey
            );
        } catch (NEPException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    private static final Set<String> GRAB_SCREEN_HEADERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(HttpHeaders.CONTENT_TYPE.toLowerCase(), HttpHeaders.CONTENT_LENGTH.toLowerCase())));

    protected void grabScreen(@NonNull String identifier, @NonNull String time, @NonNull BiConsumer<String, String> headers, @NonNull OutputStream outputStream, String itemizeUrl, Supplier<String> key) throws NEPException {
        HttpClientContext clientContext = HttpClientContext.create();
        String framegrabber = itemizeUrl + "/api/framegrabber?identifier=" + identifier + "&time=" + time;
        HttpGet get = new HttpGet(framegrabber);
        authenticate(get, key);
        get.addHeader(new BasicHeader(HttpHeaders.ACCEPT, APPLICATION_OCTET_STREAM.toString()));
        headers.accept(NPO_DISPATCHED_TO, framegrabber);
        log.info("Getting {}", framegrabber);
        try (CloseableHttpResponse execute = httpClient.execute(get, clientContext)) {
            if (execute.getStatusLine().getStatusCode() == 200) {
                for (Header h : execute.getAllHeaders()) {
                    if (GRAB_SCREEN_HEADERS.contains(h.getName().toLowerCase())) {
                        headers.accept(h.getName(), h.getValue());
                    }
                }
                IOUtils.copy(execute.getEntity().getContent(), outputStream);
            } else {
                StringWriter result = new StringWriter();
                IOUtils.copy(execute.getEntity().getContent(), result, Charset.defaultCharset());
                throw new RuntimeException(result.toString());
            }
        } catch (Exception e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public void grabScreenMid(String mid, Duration offset, @NonNull BiConsumer<String, String> headers, OutputStream outputStream) throws NEPException {
        String durationString = DurationFormatUtils.formatDuration(offset.toMillis(), "HH:mm:ss.SSS", true);
        try {
            grabScreen(mid, durationString, headers, outputStream, itemizeMidUrl, itemizeMidKey);
        } catch (NEPException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public void grabScreenLive(String channel, Instant instant, @NonNull BiConsumer<String, String> headers, OutputStream outputStream) throws NEPException {
        try {
            grabScreen(channel,
                NEPItemizeRequest.fromInstant(instant)
                    .orElseThrow(() -> new IllegalArgumentException("Instant " + instant + " could not be formatted")),
                headers, outputStream, itemizeLiveUrl, itemizeLiveKey);
        } catch (NEPException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public String getLiveItemizerString() {
        return itemizeLiveUrl;
    }

    @Override
    public String getMidItemizerString() {
        return itemizeMidUrl;
    }

    @Override
    public ItemizerStatusResponse getLiveItemizerJobStatus(String jobId) {
        return getItemizerJobStatus(itemizeLiveUrl, itemizeLiveKey, jobId);
    }

    @Override
    public ItemizerStatusResponse getMidItemizerJobStatus(String jobId) {
        return getItemizerJobStatus(itemizeMidUrl, itemizeMidKey, jobId);
    }


    protected ItemizerStatusResponse getItemizerJobStatus(String url, Supplier<String> key, String jobId) {
        String jobs = url + "/api/itemizer/jobs/" + jobId + "/status";
        HttpGet get = new HttpGet(jobs);
        authenticate(get, key);
        HttpClientContext clientContext = HttpClientContext.create();

        try (CloseableHttpResponse execute = httpClient.execute(get, clientContext)) {
            if (execute.getStatusLine().getStatusCode() == 200) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(execute.getEntity().getContent(), outputStream);
                return Jackson2Mapper.getLenientInstance().readValue(outputStream.toByteArray(), ItemizerStatusResponse.class);
            }
            if (execute.getStatusLine().getStatusCode() == 404) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                IOUtils.copy(execute.getEntity().getContent(), outputStream);
                FailureResponse failureResponse = Jackson2Mapper.getLenientInstance().readValue(outputStream.toByteArray(), FailureResponse.class);
                throw new ItemizerStatusException(404, failureResponse);
            } else {
                StringWriter result = new StringWriter();
                IOUtils.copy(execute.getEntity().getContent(), result, Charset.defaultCharset());
                throw new ItemizerStatusException(execute.getStatusLine().getStatusCode(), result.toString());
            }
        } catch (NEPException nepException) {
            throw nepException;
        } catch (Exception e) {
            throw new NEPException(e, e.getMessage());
        }
    }


    private void authenticate(HttpUriRequest request, Supplier<String> key) {
        request.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, key.get()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":l:" + itemizeLiveUrl + ",m:" + itemizeMidUrl;
    }

    @Override
    @PreDestroy
    public synchronized void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
            httpClient = null;
        }
    }

}


