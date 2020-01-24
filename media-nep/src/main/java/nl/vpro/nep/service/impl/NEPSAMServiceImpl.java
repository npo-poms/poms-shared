package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;

import org.apache.http.HttpHeaders;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.sam.api.AccessApi;
import nl.vpro.nep.sam.model.*;
import nl.vpro.nep.service.NEPSAMService;

/**
 * https://jira.vpro.nl/browse/MSE-3754
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
@Named("NEPSAMService")
public class NEPSAMServiceImpl implements NEPSAMService {

    private String providerLive = "npo";
    private String platformLive = "npo";
    private String drmProfileLive = "dash";

    private String providerMid = "npo";
    private String platformMid = "npo";
    private String drmProfileMid = "dash";
    private String noDrmProfileMid = "dash";

    final Supplier<String> authenticatorMid;
    final Supplier<String> authenticatorLive;

    final String baseUrlMid;
    final String baseUrlLive;

    private Duration connectTimeout = Duration.ofMillis(1000);
    private Duration socketTimeout = Duration.ofMillis(1000);

    Client httpClient = null;

    @Inject
    public NEPSAMServiceImpl(
        @Value("${nep.sam-api.mid.baseUrl}") @NonNull String baseUrlMid,
        @Value("${nep.sam-api.provider}") String providerMid,
        @Value("${nep.sam-api.platform}") String platformMid,
        @Value("${nep.sam-api.profile.drm}") String drmProfileMid,
        @Value("${nep.sam-api.profile.nodrm}") String noDrmProfileMid,
        @Value("${nep.sam-api.live.baseUrl}") @NonNull String baseUrlLive,
        @Value("${nep.sam-api.provider}") String providerLive,
        @Value("${nep.sam-api.platform}") String platformLive,
        @Value("${nep.sam-api.profile.drm}") String drmProfileLive,
        @Named("NEPSAMAuthenticatorMid") @NonNull Supplier<String> authenticatorMid,
        @Named("NEPSAMAuthenticatorLive") @NonNull Supplier<String> authenticatorLive) {
        this.authenticatorMid = authenticatorMid;
        this.authenticatorLive = authenticatorLive;
        this.baseUrlMid = baseUrlMid;
        this.providerMid = providerMid == null ? this.providerMid : providerMid;
        this.platformMid = platformMid == null ? this.platformMid : platformMid;
        this.drmProfileMid = drmProfileMid == null ? this.drmProfileMid : drmProfileMid;
        this.noDrmProfileMid = noDrmProfileMid == null ? this.noDrmProfileMid: noDrmProfileMid;
        this.baseUrlLive = baseUrlLive;
        this.providerLive = providerLive == null ? this.providerLive : providerLive;
        this.platformLive = platformLive == null ? this.platformLive : platformLive;
        this.drmProfileLive  = drmProfileLive == null ? this.drmProfileLive : drmProfileLive;
    }

    @PostConstruct
    public void log() {
        log.info("Connecting with {}/{}", this.baseUrlMid, this.baseUrlLive);
    }

    @Override
    @SneakyThrows
    public String streamAccessLive(String channel,  String ip, Duration duration) {
        StreamAccessItem request = createStreamAccessItem(ip, duration);
        AccessApi streamApi = getStreamApi(baseUrlLive, authenticatorLive);
        String profile = drmProfileLive;
        log.info("Using profile {}",profile);
        StreamAccessResponseItem streamAccessResponseItem = streamApi.v2AccessProviderProviderNamePlatformPlatformNameProfileProfileNameStreamStreamIdPost(providerLive, platformLive,  profile, channel, request);
        Map<String, Object> attributes = (Map<String, Object>) streamAccessResponseItem.getData().getAttributes();
        return (String) attributes.get("url");
    }

    @Override
    @SneakyThrows
    public String streamAccessMid(String mid, boolean drm, String ip, Duration duration) {
        StreamAccessItem request = createStreamAccessItem(ip, duration);
        AccessApi streamApi = getStreamApi(baseUrlMid, authenticatorMid);
        String profile = drm ? drmProfileMid : noDrmProfileMid;
        log.info("Using profile {}",profile);
        StreamAccessResponseItem streamAccessResponseItem = streamApi.v2AccessProviderProviderNamePlatformPlatformNameProfileProfileNameStreamStreamIdPost(providerMid, platformMid,  profile, mid, request);
        Map<String, Object> attributes = (Map<String, Object>) streamAccessResponseItem.getData().getAttributes();
        return (String) attributes.get("url");
    }


    static StreamAccessItem createStreamAccessItem(String ip, Duration duration) {
        StreamAccessItem item = new StreamAccessItem().data(new ApiObject().type("access"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("viewer", "pomsgui");
        attributes.put("ip", ip);
        attributes.put("duration", duration);
        item.getData().setAttributes(attributes);
        return item;
    }

    AccessApi getStreamApi(String baseUrl, Supplier<String> authenticator) {
        AccessApi streamApi = new AccessApi();
        streamApi.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authenticator.get());
        streamApi.getApiClient().setBasePath(baseUrl);
        streamApi.getApiClient().setHttpClient(getHttpClient());
        return streamApi;
    }
    private Client getHttpClient() {
        if (httpClient == null) {
            ResteasyClientBuilder builder = new ResteasyClientBuilderImpl();
            builder.connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS);
            builder.readTimeout(socketTimeout.toMillis(), TimeUnit.MILLISECONDS);
            httpClient = builder.build();
        }
        return httpClient;
     }


    @Override
    public String toString() {
        return getClass().getSimpleName() + ":l:" + baseUrlLive + ",m:" + baseUrlMid;
    }

}
