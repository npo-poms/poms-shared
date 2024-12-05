package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import jakarta.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.util.TimeUtils;

import static nl.vpro.nep.service.impl.NEPItemizeServiceImpl.JSON;

/**
 * See also <a href="https://jira.vpro.nl/browse/MSE-4435">JIRA</a>
 * <p>
 * TODO We have generated API for this too.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
@ManagedResource
public class NEPSAMAuthenticator implements Supplier<String>, BooleanSupplier {
    private final LoginRequest loginRequest;
    @VisibleForTesting
    LoginResponse loginResponse;
    private Instant responseInstant;
    private final String baseUrl;

    private Duration maxAge = Duration.ofHours(6);

    public NEPSAMAuthenticator(
        @Value("${nep.sam-api.username}") String username,
        @Value("${nep.sam-api.password}") String password,
        @Value("${nep.sam-api.baseUrl}") String baseUrl
    ) {
        this.loginRequest = new LoginRequest(username, password);
        this.baseUrl = baseUrl;
    }

    @PostConstruct
    public void log() {
        log.info("Authenticating with {}", this.baseUrl);
    }

    @Override
    @SneakyThrows
    public String get() {
        if (needsRefresh()) {
            authenticate();
        }
        return "Bearer " + this.loginResponse.getToken();
    }

    private static final ObjectMapper LENIENT = Jackson2Mapper.getLenientInstance();

    @Override
    public boolean getAsBoolean() {
        return needsRefresh();
    }

    protected void authenticate() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.custom()
            .build()) {
            String playerUrl = baseUrl + "/v2/token";
            HttpClientContext clientContext = HttpClientContext.create();

            String json = LENIENT.writeValueAsString(loginRequest);
            StringEntity entity = new StringEntity(json, JSON);
            HttpPost httpPost = new HttpPost(playerUrl);
            httpPost.addHeader(new BasicHeader(HttpHeaders.ACCEPT, JSON.toString()));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, clientContext);
            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            this.loginResponse = LENIENT.readValue(response.getEntity().getContent(), LoginResponse.class);
            this.responseInstant = Instant.now();
            log.info("Acquired {} (expires {})", this.loginResponse, getExpiration());
        }
    }

    @ManagedAttribute
    @SneakyThrows
    public Instant getExpiration() {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = loginResponse.token.split("\\."); // Splitting header, payload and signature
        JsonNode node = new ObjectMapper().readTree(decoder.decode(parts[1]));
        return Instant.ofEpochSecond(node.get("exp").intValue());
    }


    @ManagedAttribute
    public String getMaxAge() {
        return maxAge.toString();
    }
    public void setMaxAge(String s){
        this.maxAge = TimeUtils.parseDuration(s).orElse(this.maxAge);
    }

    @ManagedOperation
    public void invalidate() {
        this.loginResponse = null;
    }

    /**
     * Returns false if the key is not valid for at least one day.
     */
    @ManagedAttribute
    public boolean needsRefresh() {
        return needsRefresh(Instant.now());
    }

     /**
     * Returns false if the key is not valid for at least one day.
     */
     public boolean needsRefresh(Instant now) {
        if (loginResponse == null) {
            return true;
        }
         if (Duration.between(responseInstant, now).compareTo(maxAge) > 0) {
            return true;
        }
        return getExpiration().isBefore(
            now.plus(Duration.ofDays(1))
        );

    }


    @lombok.Value
    public static class LoginRequest {
        String username;
        String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @lombok.Value
    public static class LoginResponse {
        String token = null;

        @JsonIgnore
        Instant obtaintedAt = Instant.now();
    }
}
