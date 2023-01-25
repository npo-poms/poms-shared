package nl.vpro.domain.image;

import org.checkerframework.checker.nullness.qual.*;

import nl.vpro.util.URLPathEncode;

/**
 * This services knows how to create urls from image ids.
 *
 * @author Michiel Meeuwissen
 * @since 7.2
 */
public interface ImageUrlService {


    /**
     * Returns the base url for image 'api' calls.
     * For the backend server this is e.g. {@code https://poms-test.omroep.nl/images/api}
     * for a frontend server this is e.g. {@code https://images-test.poms.omroep.nl/}
     */
    String getImageBaseUrl();

    @PolyNull
    default String getOriginalUrl(@PolyNull Long id) {
        if (id == null) {
            return null;
        }
        String imageServerBaseUrl = getImageBaseUrl();
        StringBuilder result = new StringBuilder();
        result.append(imageServerBaseUrl).append(id);
        appendSecurityTokens(result);
        return result.toString();
    }


    /**
     * Resolves a web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri id.
     */
    default String getImageLocation(@NonNull Long  id , @Nullable String fileExtension, boolean encode, String... conversions) {
        String imageServerBaseUrl = getImageBaseUrl();
        StringBuilder builder = new StringBuilder(imageServerBaseUrl);
        for (String conversion : conversions) {
            if (encode) {
                builder.append(URLPathEncode.encode(conversion));
            } else {
                builder.append(conversion);
            }
            builder.append('/');
        }
        builder.append(id);
        if (fileExtension != null) {
            builder.append('.').append(fileExtension);
        }
        appendSecurityTokens(builder);
        return builder.toString();
    }

    /**
     * Defaulting version of {@link #getImageLocation(Long, String, boolean, String...)} (with {@code true})
     */
    default String getImageLocation(@NonNull Long  id , @Nullable String fileExtension, String... conversions) {
        return getImageLocation(id, fileExtension, true, conversions);
    }

    default void appendSecurityTokens(StringBuilder builder) {

    }

}
