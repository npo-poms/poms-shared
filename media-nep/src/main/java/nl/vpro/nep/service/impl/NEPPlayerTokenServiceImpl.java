package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Duration;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.nep.domain.PlayreadyRequest;
import nl.vpro.nep.domain.PlayreadyResponse;
import nl.vpro.nep.domain.WideVineRequest;
import nl.vpro.nep.domain.WideVineResponse;
import nl.vpro.nep.service.NEPPlayerTokenService;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Named("NEPTokenService")

@Slf4j
public class NEPPlayerTokenServiceImpl implements NEPPlayerTokenService {

    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseUrl;// =

    CloseableHttpClient httpClient = null;


    private Duration connectTimeout = Duration.ofMillis(1000);
    private Duration connectionRequestTimeout =  Duration.ofMillis(1000);
    private Duration socketTimeout = Duration.ofMillis(1000);

    public NEPPlayerTokenServiceImpl(
        @Value("${nep.tokengenerator-api.baseUrl}") String baseUrl) {
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
            HttpPost httpPost = new HttpPost(baseUrl + "/widevine/npo");
            httpPost.setEntity(entity);
            //httpPost.addHeader(HttpHeaders.AUTHORIZATION, authenticator.get());
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
            HttpPost httpPost = new HttpPost(baseUrl + "/playready/npo");
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
