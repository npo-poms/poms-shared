package nl.vpro.domain.media.support;

import org.checkerframework.checker.nullness.qual.*;

/**
 * This services knows how to create urls from image ids.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface ImageUrlService extends nl.vpro.domain.image.ImageUrlService {

    default Long getId(@NonNull Image image) {
        return getIdFromImageUri(image.getImageUri());
    }

    @Nullable
    default Long getIdFromImageUri(@Nullable String imageUri) {
        return Image.getIdFromImageUri(imageUri);
    }

    @Nullable
    default String getOriginalUrlFromImageUri(@Nullable String imageUri) {
        return getOriginalUrl(getIdFromImageUri(imageUri));
    }


    /**
     * Resolves a web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @since 7.2
     */
    @PolyNull
    default String getImageLocation(@PolyNull String uri ,  @Nullable String fileExtension, boolean encode, String... conversions) {
        Long id = getIdFromImageUri(uri);
        if (id == null) {
            return null;
        }
        return getImageLocation(id,  fileExtension, encode,  conversions);
    }


}
