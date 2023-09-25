/*
 * Copyright (C) 2018 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.npoplayer;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import nl.vpro.api.client.resteasy.AbstractApiClient;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.simple.Level;
import nl.vpro.rs.npoplayer.NPOPlayerApiRestService;
import nl.vpro.util.ConfigUtils;
import nl.vpro.util.Env;

/**
 * This created a resteasy client using {@link nl.vpro.rs.npoplayer.NPOPlayerRestService}.
 *
 * This basically does this:
 * <pre>{@code curl -H'ApiKey: <your key>' https://start-player-api.npo.nl/video/VPWON_1262643/init -d '{"id":"__vpronpoplayer__0","stylesheet":"https://files.vpro.nl/npoplayer/8/controls.css","autoplay":true}'
 * }
 * </pre>
 * @author r.jansen
 * @since 5.10
 * @Deprecated
 */
@Named
@Deprecated
public class NPOPlayerApiClient extends AbstractApiClient {
    private final static String APIKEYHEADER = "ApiKey";
    private final String apiKey;

    NPOPlayerApiRestService restService;

    public static NPOPlayerApiClient.Builder configured(Env env) {
        return ConfigUtils.configuredInHome(env,
            builder(), "npoplayerapi-client.properties");
    }

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    public static class Builder implements javax.inject.Provider<NPOPlayerApiClient> {
        @Inject
        @Named("npoplayerapi-rs.url")
        String baseUrl;
        @Inject
        @Named("npoplayerapi-rs.apikey")
        String apiKey;

        @Override
        public NPOPlayerApiClient get() {
            return build();
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    protected NPOPlayerApiClient(
        @NonNull String baseUrl,
        Duration connectionRequestTimeout,
        Duration connectTimeout,
        Duration socketTimeout,
        Integer maxConnections,
        Integer maxConnectionsPerRoute,
        Integer maxConnectionsNoTimeout,
        Integer maxConnectionsPerRouteNoTimeout,
        Duration connectionInPoolTTL,
        Duration validateAfterInactivity,
        Duration countWindow,
        Integer bucketCount,
        Duration warnThreshold,
        Level warnLevel,
        List<Locale> acceptableLanguages,
        MediaType accept,
        MediaType contentType,
        Boolean trustAll,
        Jackson2Mapper objectMapper,
        String mbeanName,
        @NonNull String apiKey,
        ClassLoader classLoader,
        Boolean registerMBean,
        boolean eager) {
        super(
            baseUrl,
            connectionRequestTimeout,
            connectTimeout,
            socketTimeout,
            maxConnections,
            maxConnectionsPerRoute,
            maxConnectionsNoTimeout,
            maxConnectionsPerRouteNoTimeout,
            connectionInPoolTTL,
            validateAfterInactivity,
            countWindow,
            bucketCount,
            warnThreshold,
            warnLevel,
            acceptableLanguages,
            accept,
            contentType,
            trustAll,
            objectMapper,
            mbeanName,
            classLoader,
            NPOPlayerApiClient.class.getName(),
            registerMBean,
            eager
        );
        this.apiKey = apiKey;
    }

    @Override
    protected Stream<Supplier<?>> services() {
        return Stream.of(
            this::getRestService
        );
    }

    public NPOPlayerApiRestService getRestService() {
        if (restService == null) {
            restService = proxyErrorsAndCount(
                NPOPlayerApiRestService.class,
                getTarget(getClientHttpEngine()).proxyBuilder(NPOPlayerApiRestService.class).build());
        }
        return restService;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        restService = null;
    }

    @Override
    public String toString() {
        return "client for " + apiKey + "@" + baseUrl;
    }

    @Override
    protected void buildResteasy(ResteasyClientBuilder resteasyClientBuilder) {
        resteasyClientBuilder.register((ClientRequestFilter) requestContext -> requestContext.getHeaders().add(APIKEYHEADER, apiKey));
    }

    public static Builder builderWithoutMBean() {
        return builder().registerMBean(false);
    }
}
