package nl.vpro.media.tva.bindinc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.inject.Inject;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.media.broadcaster.BroadcasterServiceImpl;

/**
 * Extends {@link BroadcasterServiceImpl} but adds some overrides for bindinc.
 * @author Michiel Meeuwissen
 * @since 5.33
 */
@Slf4j
public class BindincBroadcasterService extends BroadcasterServiceImpl {


    private final Set<String> warned = new CopyOnWriteArraySet<>();
    private final Map<String, String> bindincOverride = new TreeMap<>();
    {
        try {
            Properties properties = new Properties();
            properties.load(BindincBroadcasterService.class.getResourceAsStream("/bindinc.broadcasters.properties"));
            properties.entrySet().forEach(e -> {
                bindincOverride.put((String) e.getKey(), (String) e.getValue());
            });
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Inject
    public BindincBroadcasterService(@Value("${broadcasters.repository.location}") String configFile) {
        super(configFile, false, true);
    }

    @Override
    public Broadcaster find(String id) {
        Broadcaster f = super.find(id);
        if (f == null) {
            String bindinc = bindincOverride.get(id);
            if (bindinc != null) {
                Broadcaster b;
                if (bindinc.startsWith(":")) {
                    String otherId = bindinc.substring(1);
                    if (otherId.isEmpty()) {
                        return new Broadcaster("");
                    } else {
                        b = new Broadcaster(id, otherId);
                    }
                } else {
                    b = super.find(bindinc);
                }
                if (warned.add(id)) {
                    log.info("Found bindinc override {} -> {}", id, b);
                }
                return b;
            }
        }
        return f;
    }

    @Override
    public String toString() {
        return super.toString() + " (overriding: " + bindincOverride.keySet() + ")";
    }

}
