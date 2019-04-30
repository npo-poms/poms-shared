package nl.vpro.nep.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.function.Supplier;

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

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.util.DateUtils;

import static nl.vpro.nep.service.impl.NEPItemizeServiceImpl.JSON;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class NEPItemizerAuthenticator implements Supplier<String> {



    private final LoginRequest loginRequest;
    LoginResponse loginResponse;
    private final String baseUrl;

    public NEPItemizerAuthenticator(
        @Value("${nep.itemizer.username}") String username,
        @Value("${nep.itemizer.password}") String password,
         @Value("${nep.itemizer.baseUrl}") String baseUrl
        ) {
        this.loginRequest = new LoginRequest(username, password);
        this.baseUrl = baseUrl;
    }


    @Override
    @SneakyThrows
    public String get() {
        if (loginResponse == null || loginResponse.needsRefresh()) {
            authenticate();
        }
        return "Bearer " + this.loginResponse.getToken();

    }

    protected void authenticate() throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.custom()
            .build()) {
            String playerUrl = baseUrl + "/token";
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
        }

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

        public LoginResponse() {

        }
        @JsonIgnore
        public Jws<Claims> getJws() {
            return Jwts.parser()
                .setSigningKey("foobar")
                .parseClaimsJws(token);
        }

        public boolean needsRefresh() {
            Instant expiration = DateUtils.toInstant(getJws().getBody().getExpiration());
            return expiration.isBefore(Instant.now());

        }
        public Instant getExpiration() {
            return DateUtils.toInstant(getJws().getBody().getExpiration());

        }

    }
}
