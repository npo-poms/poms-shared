package nl.vpro.domain.media.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public enum StreamingStatus {

    /**
     * Not notified by NEP
     */
    NOT_AVAILABLE,
    /**
     * Explicitely notified by NEP to be offline
     */
    OFFLINE,
    /**
     * Explicitely notified by NEP to be online
     */
    AVAILABLE,
    /**
     * Explicitely notified by NEP to be offline with DRM
     */
    AVAILABLE_WITH_DRM
    ;

    public static boolean isAvailable(StreamingStatus status) {
        return status == AVAILABLE || status == AVAILABLE_WITH_DRM;
    }
    public static StreamingStatus available(boolean drm) {
        return drm ? StreamingStatus.AVAILABLE_WITH_DRM : StreamingStatus.AVAILABLE;
    }

    public static Collection<StreamingStatus> availableStatuses() {
        return Arrays.stream(values()).filter(StreamingStatus::isAvailable).collect(Collectors.toSet());
    }


    public static Collection<StreamingStatus> notAvailableStatuses() {
        return Arrays.stream(values()).filter(s -> ! isAvailable(s)).collect(Collectors.toSet());
    }

    public boolean hasDrm() {
        return this == AVAILABLE_WITH_DRM;
    }
}
