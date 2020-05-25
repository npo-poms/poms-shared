package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.service.NEPItemizeService;

import static org.apache.http.entity.ContentType.APPLICATION_OCTET_STREAM;

/**
 * See also https://jira.vpro.nl/browse/MSE-4435
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPItemizeService")
@Slf4j
public class NEPItemizeServiceImpl implements NEPItemizeService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");


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

    @SneakyThrows
    protected NEPItemizeResponse itemize(@NonNull NEPItemizeRequest request, String itemizeUrl, Supplier<String> itemizeKey) {
        String playerUrl = itemizeUrl + "/api/itemizer/job";
        log.info("Itemizing {} @ {}", request, playerUrl);
        HttpClientContext clientContext = HttpClientContext.create();
        String json = Jackson2Mapper.getLenientInstance().writeValueAsString(request);
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
        }

    }

    @Override
    public NEPItemizeResponse itemize(String channel, Instant start, Instant end, Integer max_bitrate) {
        return itemize(
            NEPItemizeRequest.builder()
                .identifier(channel)
                .starttime(NEPItemizeRequest.fromInstant(start).orElseThrow(IllegalArgumentException::new))
                .endtime(NEPItemizeRequest.fromInstant(end).orElseThrow(IllegalArgumentException::new))
                .max_bitrate(max_bitrate)
                .build(),
            itemizeLiveUrl,
            itemizeLiveKey
        );
    }

    @Override
    public NEPItemizeResponse itemize(String mid, Duration start, Duration end, Integer max_bitrate) {
        return itemize(
            NEPItemizeRequest.builder()
                .starttime(NEPItemizeRequest.fromDuration(start, Duration.ZERO))
                .endtime(NEPItemizeRequest.fromDuration(end).orElseThrow(IllegalArgumentException::new))
                .identifier(mid).max_bitrate(max_bitrate).build(),
            itemizeMidUrl,
            itemizeMidKey
        );
    }

    @SneakyThrows
    protected void grabScreen(@NonNull String identifier, @NonNull String time, @NonNull  OutputStream outputStream, String itemizeUrl, Supplier<String> key) {
        HttpClientContext clientContext = HttpClientContext.create();
        String framegrabber = itemizeUrl + "/api/framegrabber?identifier=" + identifier + "&time=" + time;
        HttpGet get = new HttpGet(framegrabber);
        authenticate(get, key);
        get.addHeader(new BasicHeader(HttpHeaders.ACCEPT, APPLICATION_OCTET_STREAM.toString()));
        log.info("Getting {}", framegrabber);
        try (CloseableHttpResponse execute = httpClient.execute(get, clientContext)) {
            if (execute.getStatusLine().getStatusCode() == 200) {
                IOUtils.copy(execute.getEntity().getContent(), outputStream);
            } else {
                StringWriter result = new StringWriter();
                IOUtils.copy(execute.getEntity().getContent(), result, Charset.defaultCharset());
                throw new RuntimeException(result.toString());
            }
        }
    }

    @Override
    public void grabScreen(String mid, Duration offset, OutputStream outputStream) {
        String durationString = DurationFormatUtils.formatDuration(offset.toMillis(), "HH:mm:ss.SSS", true);
        grabScreen(mid, durationString, outputStream, itemizeMidUrl, itemizeMidKey);
    }

    @Override
    public void grabScreen(String channel, Instant instant, OutputStream outputStream) {
        grabScreen(channel,
            FORMATTER.format(instant.atZone(ZoneId.of("UTC")).toLocalDateTime()), outputStream, itemizeLiveUrl, itemizeLiveKey);
    }



    private void authenticate(HttpUriRequest request, Supplier<String> key) {
        request.addHeader(new BasicHeader(HttpHeaders.AUTHORIZATION, key.get()));


    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":l:"  + itemizeLiveUrl + ",m:" + itemizeMidUrl;
    }
}
