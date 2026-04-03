package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A container to statically  contain one {@link ImageUrlService}.
 * <p>
 * See {@link #getInstance()}
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class ImageUrlServiceHolder extends nl.vpro.domain.image.ImageUrlServiceHolder {


    private ImageUrlServiceHolder() {
    }


    @NonNull
    public static ImageUrlService getInstance() {
        nl.vpro.domain.image.ImageUrlService wrapped = nl.vpro.domain.image.ImageUrlServiceHolder.getInstance();
        return wrapped::getImageBaseUrl;
    }

     /**
     * Resolves a web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    public static String getImageLocation(@NonNull Image image, String fileExtension, String... conversions) {
        ImageUrlService instance = getInstance();
        Long id = instance.getId(image);
        if (id == null) {
            return null;
        }
        return instance.getImageLocation(id, fileExtension, conversions);
    }
}
