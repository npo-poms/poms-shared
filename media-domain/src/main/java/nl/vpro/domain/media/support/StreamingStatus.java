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
    NOT_AVAILABLE(null, false),

    /**
     * Explicitely notified by NEP to be offline
     */
    OFFLINE(null, false),

    /**
     * Explicitely notified by NEP to be online
     */
    AVAILABLE(false, true),

    /**
     * Explicitely notified by NEP to be online with DRM
     */
    AVAILABLE_WITH_DRM(true, true),

    /**
     * Explicitely notified by NEP to be online with DRM and AES
     * @since 5.6
     */
    AVAILABLE_WITH_AES_DRM(true, true);

    private final Boolean drm;
    private final boolean available;

    StreamingStatus(Boolean drm, boolean available) {
        this.drm = drm;
        this.available = available;
    }

    public static StreamingStatus available(boolean drm) {
        return drm ? StreamingStatus.AVAILABLE_WITH_DRM : StreamingStatus.AVAILABLE;
    }

    public static Collection<StreamingStatus> availableStatuses() {
        return Arrays.stream(values())
            .filter(StreamingStatus::isAvailable)
            .collect(Collectors.toSet());
    }


    public static Collection<StreamingStatus> notAvailableStatuses() {
        return Arrays.stream(values()).filter(s -> ! s.isAvailable()).collect(Collectors.toSet());
    }

    public boolean hasDrm() {
        return drm != null && drm;
    }

    public boolean isAvailable() {
        return available;
    }

}
