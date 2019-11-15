package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.jackson2.Jackson2Mapper;

import static nl.vpro.nep.service.impl.NEPItemizeServiceImpl.JSON;

/**
 * See also https://jira.vpro.nl/browse/MSE-4435
 *
 * TODO We have generated API for this too.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPSAMAuthenticator implements Supplier<String> {


    private static final Duration DEFAULT_EXPIRY = Duration.ofDays(7);

    private final LoginRequest loginRequest;
    LoginResponse loginResponse;
    private final String baseUrl;

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

    protected void authenticate() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.custom()
            .build()) {
            String playerUrl = baseUrl + "/v2/token";
            HttpClientContext clientContext = HttpClientContext.create();

            String json = Jackson2Mapper.getLenientInstance().writeValueAsString(loginRequest);
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

            this.loginResponse = Jackson2Mapper.getLenientInstance().readValue(response.getEntity().getContent(), LoginResponse.class);
            log.info("Acquired {}", this.loginResponse);
        }

    }


    @SneakyThrows
    public Instant getExpiration() {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] parts = loginResponse.token.split("\\."); // Splitting header, payload and signature
        JsonNode node = new ObjectMapper().readTree(decoder.decode(parts[1]));
        return Instant.ofEpochSecond(node.get("exp").intValue());
    }


    /**
     * Returns false if the key is not valid for at least one day.
     */
    public boolean needsRefresh() {
        if (loginResponse == null) {
            return true;
        }
        return getExpiration().isBefore(
            Instant.now().plus(Duration.ofDays(1))
        );

    }


    @lombok.Value
    public static class LoginRequest {

        private final String username;
        private final String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @lombok.Value
    public static class LoginResponse {
        private String token = null;


        @JsonIgnore
        private final Instant obtaintedAt = Instant.now();


    }
}
