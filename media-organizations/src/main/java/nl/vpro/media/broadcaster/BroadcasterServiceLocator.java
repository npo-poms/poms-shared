package nl.vpro.media.broadcaster;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class BroadcasterServiceLocator {

    private static BroadcasterService singleton;


    @Inject
    private Provider<BroadcasterService> broadcasterServiceProvider;

    @PostConstruct
    public void setSingleton() {
        singleton = broadcasterServiceProvider.get();
    }


    public static BroadcasterService getInstance() {
        return singleton;
    }

    public static List<String> getIds() {
        return getInstance().findAll().stream().map(Broadcaster::getId).collect(Collectors.toList());
    }

}
