package nl.vpro.domain.media.support;

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

    public boolean hasDrm() {
        return this == AVAILABLE_WITH_DRM;
    }
}
