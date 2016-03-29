/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.media.broadcaster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

/**
 * @author rico
 * @since 3.0
 */
public class BroadcasterServiceImpl implements BroadcasterService {

    protected static final Logger LOG = LoggerFactory.getLogger(BroadcasterServiceImpl.class);

    private final String configFile;

    private Map<String, Broadcaster> repository;


    public BroadcasterServiceImpl(String configFile) {
        this.configFile = configFile;
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
        if (repository == null) {
            synchronized (this) {
                if (repository == null) {
                    Properties broadcastersConfig = new Properties();
                    Map<String, Broadcaster> result = new HashMap<>();
                    try {
                        InputStream inputStream;
                        if (configFile.startsWith("classpath:")) {
                            inputStream = getClass().getClassLoader().getResourceAsStream(configFile.substring("classpath:".length() + 1));
                        } else {
                            inputStream = new URL(configFile).openStream();
                        }
                        InputStreamReader reader = new InputStreamReader(inputStream);
                        broadcastersConfig.load(reader);
                        for (Map.Entry<Object, Object> entry : broadcastersConfig.entrySet()) {
                            String id = (String) entry.getKey();
                            String name = (String) entry.getValue();
                            Broadcaster broadcaster = new Broadcaster(id.trim(), name.trim());
                            result.put(broadcaster.getId(), broadcaster);
                        }
                        reader.close();
                    } catch (FileNotFoundException fe) {
                        LOG.error("Can't open config file {}", configFile);
                    } catch (IOException ie) {
                        LOG.error("Can't read config file {}", configFile);
                    }
                    repository = result;
                }
            }
        }
        return repository;
    }
}
