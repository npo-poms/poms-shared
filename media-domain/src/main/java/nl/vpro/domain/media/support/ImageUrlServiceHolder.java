package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class ImageUrlServiceHolder {

    private static ImageUrlService instance;

    private ImageUrlServiceHolder() {
    }

    @NonNull
    public static ImageUrlService getInstance() {
        if (instance == null) {
            log.warn("No image backend service configured");
        }
        return instance;
    }
    public static void setInstance(@NonNull ImageUrlService instance) {
        if (ImageUrlServiceHolder.instance != null && ImageUrlServiceHolder.instance != instance) {
            log.info("Replacing image backend service with {}", instance);
        }  else {
            log.info("Setting image backend service with {}", instance);
        }
        ImageUrlServiceHolder.instance = instance;
    }

}
