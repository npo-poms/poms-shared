package nl.vpro.media.broadcaster;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class BroadcasterServiceLocator {

    private static volatile BroadcasterService singleton;


    @Inject
    private Provider<BroadcasterService> broadcasterServiceProvider;

    @PostConstruct
    public void setSingleton() {
        singleton = broadcasterServiceProvider.get();
    }

    public static void setInstance(BroadcasterService service) {
        singleton = service;
    }

    public static BroadcasterService getInstance() {
        return getInstance(true);
    }


    public static BroadcasterService getInstance(boolean sync) {
        return getInstance(sync, true);
    }


    public static BroadcasterService getInstance(boolean sync, boolean needsOtherIds) {
        if (singleton == null) {
            synchronized(BroadcasterService.class) {
                singleton = new BroadcasterServiceImpl("https://poms.omroep.nl/broadcasters/", sync, needsOtherIds);
                log.warn("No broadcaster service configured, taking {} implicitely", singleton);
            }
        }
        return singleton;
    }

    public static List<String> getIds() {
        return getInstance()
            .findAll()
            .stream()
            .map(Broadcaster::getId)
            .collect(Collectors.toList());
    }

     public static List<String> getDisplayNames() {
        return getInstance()
            .findAll()
            .stream()
            .map(Broadcaster::getDisplayName)
            .collect(Collectors.toList());
    }

}
