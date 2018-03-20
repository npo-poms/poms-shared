package nl.vpro.domain.media.support;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public enum StreamingStatus implements Displayable {

    /**
     * Not notified by NEP
     */
    NOT_AVAILABLE(null, false, "niet beschikbaar"),

    /**
     * Explicitely notified by NEP to be offline
     */
    OFFLINE(null, false, "offline"),

    /**
     * Explicitely notified by NEP to be online
     */
    AVAILABLE(false, true, "beschikbaar"),

    /**
     * Explicitely notified by NEP to be online with DRM
     */
    AVAILABLE_WITH_DRM(true, true, "beschikbaar met DRM");


    private final Boolean drm;
    private final boolean available;
    @Getter
    private final String displayName;


    StreamingStatus(Boolean drm, boolean available, String displayName) {
        this.drm = drm;
        this.available = available;
        this.displayName = displayName;
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
