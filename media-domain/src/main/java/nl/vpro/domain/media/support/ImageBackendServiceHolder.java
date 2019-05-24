package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class ImageBackendServiceHolder {


    private static ImageBackendService instance;

    @Nonnull
    public static ImageBackendService getInstance() {
        if (instance == null) {
            log.warn("No image backend service configured");
        }
        return instance;
    }
    public static void setInstance(@Nonnull ImageBackendService instance) {
        if (ImageBackendServiceHolder.instance != null && ImageBackendServiceHolder.instance != instance) {
            log.info("Replacing image backend service with {}", instance);
        }  else {
            log.info("Setting image backend service with {}", instance);
        }
        ImageBackendServiceHolder.instance = instance;
    }

}
