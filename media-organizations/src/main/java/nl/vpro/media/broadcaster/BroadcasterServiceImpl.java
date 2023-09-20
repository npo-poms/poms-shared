/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Named;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.URLResource;

/**
 * @author rico
 * @since 3.0
 */
@Slf4j
public class BroadcasterServiceImpl implements BroadcasterService {


    private Map<String, Broadcaster> broadcasterMap = new TreeMap<>();
    private final URLResource<Map<String, String>> displayNameResource;
    private URLResource<Map<String, String>> misResource;
    private URLResource<Map<String, String>> whatsonResource;


    public BroadcasterServiceImpl(String configFile) {
        this(configFile, true, true);
    }

    @Inject
    @lombok.Builder
    public BroadcasterServiceImpl(
        @Named("broadcasters.repository.location") String configFile,
        @Named("broadcasters.repository.async") boolean async,
        @Named("broadcasters.repository.needsOtherIDs") boolean needsOtherIds) {
        if (! configFile.endsWith("/") && configFile.startsWith("http")) {
            configFile += "/";
        }
        this.displayNameResource = getURLResource(configFile, async);
        URI uri = URI.create(configFile);
        if (needsOtherIds && uri.getScheme().startsWith("http")) {
            setMisResource(configFile + "mis");
            setWhatsonResource(configFile + "whats_on");
        }


    }

    public void setMisResource(String configFile) {
        log.info("Using {} for mis ids", configFile);
        this.misResource = getURLResource(configFile, displayNameResource.isAsync());
    }


    public void setWhatsonResource(String configFile) {
        log.info("Using {} for what'son ids", configFile);
        this.whatsonResource = getURLResource(configFile, displayNameResource.isAsync());
    }

    protected URLResource<Map<String, String>> getURLResource(String configFile, boolean async) {
        URLResource<Map<String, String>> result = URLResource.map(URI.create(configFile), this::fillMap)
            .setMinAge(Duration.of(1, ChronoUnit.HOURS))
            .setAsync(async);
        result.setAccept("text/plain");
        result.setConnectTimeout(Duration.ofSeconds(5));
        result.setReadTimeout(Duration.ofSeconds(10));
        return result;
    }

    @Override
    public Broadcaster find(String id) {
        return getRepository().get(id);
    }

    @Override
    public List<Broadcaster> findAll() {
        return new ArrayList<>(getRepository().values());
    }

    @Override
    public Broadcaster update(Broadcaster organization) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Broadcaster organization) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + displayNameResource.getUrl() + (misResource != null ? "(/mis,whatson)" : "") + "]  " + getRepository().size() + " broadcasters";
    }

    Map<String, Broadcaster> getRepository() {
        displayNameResource.get();
        if (misResource != null) {
            misResource.get();
        }
        if (whatsonResource!= null) {
            whatsonResource.get();
        }
        return Collections.unmodifiableMap(broadcasterMap);
    }

    protected void fillMap(Map<String, String> properties) {
        Map<String, Broadcaster> result = new HashMap<>();
        for (Map.Entry<String, String> entry : displayNameResource.get().entrySet()) {
            String id = entry.getKey();
            String name = entry.getValue();
            String misId = null;
            if (misResource != null) {
                misId = misResource.get().get(id);
            }
            String whatsonId = null;
            if (whatsonResource != null) {
                whatsonId = whatsonResource.get().get(id);
            }

            String neboId = null;

            Broadcaster broadcaster = new Broadcaster(id.trim(), name.trim(), whatsonId, neboId, misId);
            result.put(broadcaster.getId(), broadcaster);

        }
        broadcasterMap = result;
    }

}
