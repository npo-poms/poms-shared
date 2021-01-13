package nl.vpro.media.tva.bindinc;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.media.broadcaster.BroadcasterServiceImpl;

/**
 * @author Michiel Meeuwissen
 * @since 5.33
 */
@Slf4j
public class BindincBroadcasterService extends BroadcasterServiceImpl {


    Set<String> warned = new CopyOnWriteArraySet<>();
    Properties bindincOverride = new Properties();
    {
        try {
            bindincOverride.load(BindincBroadcasterService.class.getResourceAsStream("/bindinc.broadcasters.properties"));
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Inject
    public BindincBroadcasterService(@Value("${broadcasters.repository.location}") String resource) {
        super(resource, false, true);
    }

    @Override
    public Broadcaster find(String id) {
        Broadcaster f = super.find(id);
        if (f == null) {
            String bindinc = bindincOverride.getProperty(id);
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

}
