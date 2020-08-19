package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;

import javax.annotation.PreDestroy;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.nep.domain.*;
import nl.vpro.nep.service.NEPPlayerTokenService;
import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPTokenService")
@ManagedResource
@Slf4j
public class NEPPlayerTokenServiceImpl implements NEPPlayerTokenService  {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseUrl;// =

    CloseableHttpClient httpClient = null;


    private Duration connectTimeout = Duration.ofMillis(1000);
    private Duration connectionRequestTimeout =  Duration.ofMillis(1000);
    private Duration socketTimeout = Duration.ofMillis(1000);

    private final String widevineKey;

    private final String playreadyKey;

    public NEPPlayerTokenServiceImpl(
        @Value("${nep.tokengenerator-api.baseUrl}") String baseUrl,
        @Value("${nep.tokengenerator-api.widevinekey}") String widevineKey,
        @Value("${nep.tokengenerator-api.playreadykey}") String playreadyKey) {
        this.baseUrl = baseUrl;
        this.widevineKey = widevineKey;
        this.playreadyKey = playreadyKey;
    }

    @PreDestroy
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }


    @Override
    @SneakyThrows
    public WideVineResponse widevineToken(String ip) {
        return MAPPER.readValue(token(ip, "widevine"), WideVineResponse.class);
    }

    @Override
    @SneakyThrows
    public PlayreadyResponse playreadyToken(String ip) {
        return MAPPER.readValue(token(ip, "playready"), PlayreadyResponse.class);
    }

    @Override
    @SneakyThrows
    public FairplayResponse fairplayToken(String ip) {
        return MAPPER.readValue(token(ip, "fairplay"), FairplayResponse.class);
    }

    @SneakyThrows
    private byte[] token(String ip, String option) {
        CloseableHttpClient client = getHttpClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String url = baseUrl + "/" + option + "/npo";
        String json = MAPPER.writeValueAsString(new TokenRequest(ip, playreadyKey));
        try {
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            IOUtils.copy(response.getEntity().getContent(), out);
            log.info("Response {}", new String(out.toByteArray()));
            return out.toByteArray();
        } catch (Exception e) {
            log.error("POST {}: {} , response {}: {}", url, json, new String(out.toByteArray()), e.getMessage());
            throw e;
        }

    }


    @Override
    @ManagedAttribute
    public String getPlayerTokenString() {
        return baseUrl;
    }

    @ManagedAttribute
    public String getConnectTimeout() {
        return String.valueOf(connectTimeout);
    }

    @ManagedAttribute
    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = TimeUtils.parseDuration(connectTimeout).orElseThrow(() -> new IllegalArgumentException("could not parse " + connectTimeout));
    }

    @ManagedAttribute
    public String getConnectionRequestTimeout() {
        return String.valueOf(connectionRequestTimeout);
    }

    @ManagedAttribute
    public void setConnectionRequestTimeout(String connectionRequestTimeout) {
        this.connectionRequestTimeout = TimeUtils.parseDuration(connectionRequestTimeout).orElseThrow(() -> new IllegalArgumentException("could not parse " + connectionRequestTimeout));
    }

    @ManagedAttribute
    public String  getSocketTimeout() {
        return String.valueOf(socketTimeout);
    }

    @ManagedAttribute
    public void setSocketTimeout(String socketTimeout) {
        this.socketTimeout = TimeUtils.parseDuration(socketTimeout).orElseThrow(() -> new IllegalArgumentException("could not parse " + socketTimeout));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" +  baseUrl;
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
