/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

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
    
    private URLResource<Map<String, Broadcaster>> resource;

    
    public BroadcasterServiceImpl(String configFile) {
        this.resource = new URLResource(URI.create(configFile), READER)
            .setMinAge(Duration.of(1, ChronoUnit.HOURS))
            .setAsync(true);
    }

    @Override
    public Broadcaster findForMisId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Broadcaster findForWhatsOnId(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Broadcaster findForNeboId(String s) {
        throw new UnsupportedOperationException();
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
        return resource.get();
    }
    
    protected static final Function<InputStream, Map<String, Broadcaster>> READER = inputStream -> {
        try {
            Properties broadcastersConfig = new Properties();
            broadcastersConfig.load(inputStream);
            Map<String, Broadcaster> result = new HashMap<>();
            for (Map.Entry<Object, Object> entry : broadcastersConfig.entrySet()) {
                String id = (String) entry.getKey();
                String name = (String) entry.getValue();
                Broadcaster broadcaster = new Broadcaster(id.trim(), name.trim());
                result.put(broadcaster.getId(), broadcaster);
            }
            return result;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    };
}
