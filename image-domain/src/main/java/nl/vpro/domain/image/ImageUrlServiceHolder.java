package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

/**
 * A container to statically  contain one {@link ImageUrlService}.
 * <p />
 * See {@link #getInstance()}
 *
 * @author Michiel Meeuwissen
 * @since 7.2
 */
@Slf4j
public class ImageUrlServiceHolder {


    public static final String IMAGE_SERVER_BASE_URL_PROPERTY = "image.server.baseUrl";

    public static Logger getLog() {
        return log;
    }

    private static ImageUrlService instance;

    protected ImageUrlServiceHolder() {
    }

    @NonNull
    public static ImageUrlService getInstance() {
        if (instance == null) {
            log.warn("No image url service configured");
        }
        return instance;
    }

    public static void setInstance() {
        String systemProperty = System.getProperty(IMAGE_SERVER_BASE_URL_PROPERTY,  "https://images.poms.omroep.nl/image/");

        assert systemProperty != null;
        setInstance(() -> systemProperty);
    }

    public static void setInstance(@NonNull ImageUrlService instance) {
        if (ImageUrlServiceHolder.instance != null && ImageUrlServiceHolder.instance != instance) {
            log.info("Replacing image url service {} with {}", ImageUrlServiceHolder.instance, instance);
        }  else {
            log.info("Setting image url service to {}", instance);
        }
        ImageUrlServiceHolder.instance = instance;
    }

}
