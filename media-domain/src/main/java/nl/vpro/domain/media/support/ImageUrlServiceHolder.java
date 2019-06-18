package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class ImageUrlServiceHolder {


    private static ImageUrlService instance;

    @Nonnull
    public static ImageUrlService getInstance() {
        if (instance == null) {
            log.warn("No image backend service configured");
        }
        return instance;
    }
    public static void setInstance(@Nonnull ImageUrlService instance) {
        if (ImageUrlServiceHolder.instance != null && ImageUrlServiceHolder.instance != instance) {
            log.info("Replacing image backend service with {}", instance);
        }  else {
            log.info("Setting image backend service with {}", instance);
        }
        ImageUrlServiceHolder.instance = instance;
    }

}
