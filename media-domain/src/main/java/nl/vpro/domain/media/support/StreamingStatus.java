package nl.vpro.domain.media.support;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public enum StreamingStatus {

    NOT_AVAILABLE,
    OFFLINE,
    AVAILABLE,
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
