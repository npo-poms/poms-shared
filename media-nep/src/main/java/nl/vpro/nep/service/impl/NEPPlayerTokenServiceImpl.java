package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Named;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.logging.simple.Level;
import nl.vpro.nep.domain.*;
import nl.vpro.nep.service.NEPPlayerTokenService;
import nl.vpro.nep.service.exception.NEPException;
import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPTokenService")
@ManagedResource
@Slf4j
public class NEPPlayerTokenServiceImpl implements NEPPlayerTokenService  {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseUrl;// =

    CloseableHttpClient httpClient = null;


    private Duration connectTimeout = Duration.ofMillis(1000);
    private Duration connectionRequestTimeout =  Duration.ofMillis(1000);
    private Duration socketTimeout = Duration.ofMillis(1000);

    private final String widevineKey;

    private final String playreadyKey;

    private final String fairplayKey;

    public NEPPlayerTokenServiceImpl(
        @Value("${nep.tokengenerator-api.baseUrl}") String baseUrl,
        @Value("${nep.tokengenerator-api.widevinekey}") String widevineKey,
        @Value("${nep.tokengenerator-api.playreadykey}") String playreadyKey
        ) {
        this.baseUrl = baseUrl;
        this.widevineKey = widevineKey;
        this.playreadyKey = playreadyKey;
        this.fairplayKey = playreadyKey;// just for completeness, we don't use this.
    }

    @Override
    @PreDestroy
    public synchronized void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
            httpClient = null;
        }
    }


    @Override
    public WideVineResponse widevineToken(String ip) throws NEPException {
        try {
            return MAPPER.readValue(token(ip, "widevine", widevineKey), WideVineResponse.class);
        } catch (IOException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public PlayreadyResponse playreadyToken(String ip) throws NEPException {
        try {
            return MAPPER.readValue(token(ip, "playready", playreadyKey), PlayreadyResponse.class);
        } catch (IOException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public FairplayResponse fairplayToken(String ip) throws NEPException {
        try {
            return MAPPER.readValue(token(ip, "fairplay", fairplayKey), FairplayResponse.class);
        } catch (IOException e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    private byte[] token(String ip, String drmType, String key) throws NEPException {
        CloseableHttpClient client = getHttpClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String url = baseUrl + "/" + drmType + "/npo";
        String json;
        try {
            json = MAPPER.writeValueAsString(new TokenRequest(ip, playreadyKey));
        } catch (JsonProcessingException e) {
            throw new NEPException(e, e.getMessage());
        }
        try {
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            IOUtils.copy(response.getEntity().getContent(), out);
            Slf4jHelper.log(log, response.getStatusLine().getStatusCode() < 300 ? Level.DEBUG: Level.ERROR, "Response {} '{}'", response.getStatusLine(), out);
            return out.toByteArray();
        } catch (Exception e) {
            log.error("POST {}: {} , response '{}': {}", url, json, out, e.getMessage());
            throw new NEPException(e, "POST " + url + ": " + json + ", response: " + out);
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

    private synchronized CloseableHttpClient getHttpClient() {
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
