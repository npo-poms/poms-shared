/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.URLResource;

/**
 * @author rico
 * @since 3.0
 */
public class BroadcasterServiceImpl implements BroadcasterService {

    private static final Logger LOG = LoggerFactory.getLogger(BroadcasterServiceImpl.class);


    private Map<String, Broadcaster> broadcasterMap = new HashMap<>();
    private URLResource<Properties> displayNameResource;
    private URLResource<Properties> misResource;
    private URLResource<Properties> whatsonResource;


    public BroadcasterServiceImpl(String configFile) {
        URI uri = URI.create(configFile);
        if (uri.getScheme().startsWith("http")) {
            setMisResource(configFile + "mis");
        }
        this.displayNameResource = getURLResource(configFile);
    }

    public void setMisResource(String configFile) {
        LOG.info("Using {} for mis ids", configFile);
        this.misResource = getURLResource(configFile);
    }


    public void setWhatsonResource(String configFile) {
        LOG.info("Using {} for what'son ids", configFile);
        this.whatsonResource = getURLResource(configFile);
    }

    protected URLResource<Properties> getURLResource(String configFile) {
        return new URLResource<>(URI.create(configFile), URLResource.PROPERTIES)
            .setMinAge(Duration.of(1, ChronoUnit.HOURS))
            .setAsync(true)
            .setCallbacks(this::fillMap);
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

    protected void fillMap(Properties properties) {
        Map<String, Broadcaster> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : displayNameResource.get().entrySet()) {
            String id = (String) entry.getKey();
            String name = (String) entry.getValue();
            String misId = null;
            if (misResource != null) {
                misId = (String) misResource.get().get(id);
            }
            String whatonId = null;
            String neboId = null;

            Broadcaster broadcaster = new Broadcaster(id.trim(), name.trim(), whatonId, neboId, misId);
            result.put(broadcaster.getId(), broadcaster);

        }
        broadcasterMap = result;
    }

}
