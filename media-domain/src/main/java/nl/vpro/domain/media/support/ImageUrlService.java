package nl.vpro.domain.media.support;

import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.*;

import nl.vpro.util.URLPathEncode;

/**
 * This services knows how to create urls from image ids.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface ImageUrlService {

    default Long getId(@NonNull Image image) {
        return getIdFromImageUri(image.getImageUri());
    }

    @Nullable
    default Long getIdFromImageUri(@Nullable String imageUri) {

        if (imageUri == null) {
            return null;
        }
        Matcher matcher = Image.SERVER_URI_PATTERN.matcher(imageUri);
        if(!matcher.find()) {
            return null;
        }

        String id = matcher.group(1);

        if(StringUtils.isEmpty(id)) {
            return null;
        }
        return Long.parseLong(id);
    }

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

    @Nullable
    default String getOriginalUrlFromImageUri(@Nullable String imageUri) {
        return getOriginalUrl(getIdFromImageUri(imageUri));
    }

    /**
     * Resolves a web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    @PolyNull
    default String getImageLocation(@PolyNull String uri , @Nullable String fileExtension, String... conversions) {
        return getImageLocation(uri, fileExtension, true, conversions);
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

    default String getImageLocation(@NonNull Long  id , @Nullable String fileExtension, String... conversions) {
        return getImageLocation(id, fileExtension, true, conversions);
    }

    default void appendSecurityTokens(StringBuilder builder) {

    }

}
