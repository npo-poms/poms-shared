package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.nep.domain.*;
import nl.vpro.nep.service.NEPSAMService;

/**
 * https://jira.vpro.nl/browse/MSE-3754
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPSAMService")
@Slf4j
public class NEPSAMServiceImpl implements NEPSAMService {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private String tokenGeneratorBaseUrl = "http://tokengenerator-npo.cdn.streamgate.nl/keys-dev/token-provider/web/authenticate";


    private String provider = "npo";
    private String platform = "npo";
    private String profile = "dash";


    final Supplier<String> authenticator;

    final String baseUrl;

    private Duration connectTimeout = Duration.ofMillis(1000);
    private Duration connectionRequestTimeout =  Duration.ofMillis(1000);
    private Duration socketTimeout = Duration.ofMillis(1000);

    CloseableHttpClient httpClient = null;


    @Inject
    public NEPSAMServiceImpl(
        @Value("${nep.sam.baseUrl}") @Nonnull String baseUrl,
        @Named("NEPSAMAuthenticator") @Nonnull Supplier<String> authenticator) {
        this.authenticator = authenticator;
        this.baseUrl = baseUrl;
    }


    @Override
    @SneakyThrows
    public WideVineResponse widevineToken(WideVineRequest request) {
        CloseableHttpClient client = getHttpClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            String json = MAPPER.writeValueAsString(request);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(tokenGeneratorBaseUrl + "/widevine/npo");
            httpPost.setEntity(entity);
            httpPost.addHeader(HttpHeaders.AUTHORIZATION, authenticator.get());
            HttpResponse response = client.execute(httpPost);
            IOUtils.copy(response.getEntity().getContent(), out);
            log.info("Response {}", new String(out.toByteArray()));
            return MAPPER.readValue(new ByteArrayInputStream(out.toByteArray()), WideVineResponse.class);
        } catch (Exception e) {
            log.error("for response {}: {}", new String(out.toByteArray()), e.getMessage());
            throw e;
        }

    }

    @Override
    @SneakyThrows
    public PlayreadyResponse playreadyToken(PlayreadyRequest request) {
        CloseableHttpClient client = getHttpClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            String json = MAPPER.writeValueAsString(request);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(tokenGeneratorBaseUrl + "/playready/npo");
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            IOUtils.copy(response.getEntity().getContent(), out);
            log.info("Response {}", new String(out.toByteArray()));
            return MAPPER.readValue(new ByteArrayInputStream(out.toByteArray()), PlayreadyResponse.class);
        } catch (Exception e) {
            log.error("for response {}: {}", new String(out.toByteArray()), e.getMessage());
            throw e;
        }
    }

    @Override
    @SneakyThrows
    public String streamUrl(String streamId, StreamUrlRequest request) {
        CloseableHttpClient client = getHttpClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Map<String, String> values = new HashMap<>();
        values.put("streamId", streamId);
        values.put("providerName", provider);
        values.put("platformName", platform);
        values.put("profileName", profile);

        URI uri = URI.create(baseUrl + StrSubstitutor.replace("access/provider/{providerName}/platform/{platformName}/profile/{profileName}/stream/{streamId}", values, "{", "}"));

        String json = MAPPER.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.create("application/vnd.api+json"));
        HttpPost httpPost = new HttpPost(uri.toString());
        httpPost.setEntity(entity);
        httpPost.addHeader(HttpHeaders.AUTHORIZATION, authenticator.get());
        HttpResponse response = client.execute(httpPost);
        IOUtils.copy(response.getEntity().getContent(), out);
        log.info("Response {}", new String(out.toByteArray()));
        return new String(out.toByteArray());
    }

     private CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            RequestConfig config = RequestConfig.custom()
                .setConnectTimeout((int) connectTimeout.toMillis())
                .setConnectionRequestTimeout((int) connectionRequestTimeout.toMillis())
                .setSocketTimeout((int) socketTimeout.toMillis())
                .build();
            httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
        }
        return httpClient;
     }
}
