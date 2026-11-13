package nl.vpro.media.broadcaster;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.ServiceLocator;

/**
 * This used to maintain its own broadcaster service, besides the one of {@link ServiceLocator}. Now it just delegates to {@link ServiceLocator}, so
 * there is only one.
 * <p>
 * The only thing it adds is that {@link #getInstance()} (and {@link #getOfflineInstance(boolean, boolean)}) fall back to a broadcaster service
 * based on poms (or on the csv in the class path), if none is configured. That service is then registered in {@link ServiceLocator} too.
 *
 * @author Michiel Meeuwissen
 * @since 5.6
 * @deprecated Use {@link ServiceLocator#getBroadcasterService()} and {@link ServiceLocator#setBroadcasterService(BroadcasterService)}. For an offline
 * broadcaster service, use {@link CSVBroadcasterService#CLASSPATH}.
 */
@Slf4j
@Deprecated(since = "8.18", forRemoval = true)

public class BroadcasterServiceLocator {

    @Inject
    private Provider<BroadcasterService> broadcasterServiceProvider;

    @PostConstruct
    public void setSingleton() {
        ServiceLocator.setBroadcasterService(broadcasterServiceProvider.get());
    }

    public static void setInstance(BroadcasterService service) {
        ServiceLocator.setBroadcasterService(service);
    }

    public static BroadcasterService getInstance() {
        return getInstance(true);
    }

    public static BroadcasterService getInstance(boolean sync) {
        return getInstance(sync, true);
    }

    /**
     * @return The broadcaster service of {@link ServiceLocator}, or if none is configured, one based on poms (which is then registered in {@link ServiceLocator})
     */
    public static BroadcasterService getInstance(boolean sync, boolean needsOtherIds) {
        return configuredOr(() -> new URLBroadcasterServiceImpl("https://poms.omroep.nl/broadcasters/", sync, needsOtherIds));
    }

    /**
     * @return The broadcaster service of {@link ServiceLocator}, or if none is configured, {@link CSVBroadcasterService#CLASSPATH} (which is then registered in {@link ServiceLocator})
     */
    public static BroadcasterService getOfflineInstance(boolean sync, boolean needsOtherIds) {
        return configuredOr(() -> CSVBroadcasterService.CLASSPATH);
    }

    private static BroadcasterService configuredOr(Supplier<BroadcasterService> fallback) {
        return ServiceLocator.findBroadcasterService().orElseGet(() -> {
            synchronized (BroadcasterServiceLocator.class) {
                return ServiceLocator.findBroadcasterService().orElseGet(() -> {
                    final BroadcasterService service = fallback.get();
                    log.warn("No broadcaster service configured, taking {} implicitly", service);
                    ServiceLocator.setBroadcasterService(service);
                    return service;
                });
            }
        });
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
