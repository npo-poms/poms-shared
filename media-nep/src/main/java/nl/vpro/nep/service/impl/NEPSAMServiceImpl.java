package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;

import org.apache.http.HttpHeaders;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.sam.api.AccessApi;
import nl.vpro.nep.sam.invoker.ApiException;
import nl.vpro.nep.sam.model.*;
import nl.vpro.nep.service.NEPSAMService;
import nl.vpro.nep.service.exception.NEPException;

/**
 * <a href="https://jira.vpro.nl/browse/MSE-3754">JIRA</a>
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
@Named("NEPSAMService")
public class NEPSAMServiceImpl implements NEPSAMService{

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

    private final Duration connectTimeout = Duration.ofMillis(1000);
    private final Duration socketTimeout = Duration.ofMillis(1000);

    Client httpClient = null;

    AccessApi accessApi;



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

    public NEPSAMServiceImpl(
         @NonNull String baseUrl,
         String provider,
         String platform,
         String drmProfile,
         String noDrmProfile,
         @NonNull Supplier<String> authenticator) {
        this(baseUrl, provider, platform, drmProfile, noDrmProfile, baseUrl, provider, platform, drmProfile, authenticator, authenticator);
    }


    @PostConstruct
    public void log() {
        log.info("Connecting with {}/{}", this.baseUrlMid, this.baseUrlLive);
    }

    @Override
    @PreDestroy
    public synchronized void close() {
        if (httpClient != null) {
            log.info("Closing {}", httpClient);
            httpClient.close();
            httpClient = null;
        }
    }

    @Override
    public Optional<String> streamAccessLive(String channel,  String ip, Duration duration) throws NEPException {
        try {
            AccessApi streamApiLive  = getStreamApi(baseUrlLive, authenticatorLive);

            StreamAccessItem request = createStreamAccessItem(ip, duration);
            String profile = drmProfileLive;
            log.debug("Using profile {}", profile);
            StreamAccessResponseItem streamAccessResponseItem = streamApiLive.v2AccessProviderProviderNamePlatformPlatformNameProfileProfileNameStreamStreamIdPost(providerLive, platformLive,  profile, channel, request);
            return getUrl(streamAccessResponseItem);

        } catch (Exception e) {
            throw new NEPException(e, e.getMessage());
        }
    }

    @Override
    public Optional<String> streamAccessMid(String mid, boolean drm, String ip, Duration duration) throws NEPException {
        try {
            AccessApi streamApiMid = getStreamApi(baseUrlMid, authenticatorMid);

            log.debug("Created {}", streamApiMid);
            StreamAccessItem request = createStreamAccessItem(ip, duration);
            String profile = drm ? drmProfileMid : noDrmProfileMid;
            log.debug("Using profile {}", profile);
            StreamAccessResponseItem streamAccessResponseItem = streamApiMid.v2AccessProviderProviderNamePlatformPlatformNameProfileProfileNameStreamStreamIdPost(providerMid, platformMid, profile, mid, request);

            return getUrl(streamAccessResponseItem);

        } catch (ApiException e) {
            if (e.getCode() == 404) {
                return Optional.empty();
            }
            throw new NEPException(e, e.getResponseBody());
        } catch (Exception e) {

            throw new NEPException(e, e.getMessage());
        }
    }

    private Optional<String> getUrl(StreamAccessResponseItem streamAccessResponseItem) {
        if (streamAccessResponseItem.getData() == null) {
            return Optional.empty();
        }
        if (streamAccessResponseItem.getData().getAttributes() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(streamAccessResponseItem.getData().getAttributes().getUrl());
    }

    @Override
    public String getStreamAccessLiveString() {
        return baseUrlLive;

    }

    @Override
    public String getStreamAccessMidString() {
        return baseUrlMid;

    }

    static StreamAccessItem createStreamAccessItem(String ip, Duration duration) {
        StreamAccessItem item = new StreamAccessItem().data(new StreamAccessItemData());
        StreamAccessAttributes attributes = new StreamAccessAttributes();
        attributes.setViewer("pomsgui");
        attributes.setIp(ip);
        attributes.setDuration(duration == null ? null : (int) duration.toMillis() / 1000 );
        item.getData().setAttributes(attributes);
        return item;
    }

    private AccessApi getStreamApi(String baseUrl, Supplier<String> authenticator) {
        if (accessApi == null || ((NEPSAMAuthenticator) authenticator).needsRefresh()) {
            ResteasyProviderFactory factor = ResteasyProviderFactory.getInstance();
            var configuration = factor.getConfiguration();
            AccessApi streamApi = new AccessApi();
            streamApi.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authenticator.get());
            streamApi.getApiClient().setHttpClient(getHttpClient());
            streamApi.getApiClient().setBasePath(baseUrl);
            accessApi = streamApi;
        }

        return accessApi;
    }

    private synchronized Client getHttpClient() {
        if (httpClient == null) {
            ResteasyClientBuilder builder = new ResteasyClientBuilderImpl();
            builder.connectTimeout(connectTimeout.toMillis(), TimeUnit.MILLISECONDS);
            builder.readTimeout(socketTimeout.toMillis(), TimeUnit.MILLISECONDS);
            httpClient = builder.build();
            log.info("Created http client {}", httpClient);
        }
        return httpClient;
     }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":l:" + baseUrlLive + ",m:" + baseUrlMid;
    }

}
