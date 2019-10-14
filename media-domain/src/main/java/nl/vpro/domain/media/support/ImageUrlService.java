package nl.vpro.domain.media.support;

import java.util.regex.Matcher;

import org.checkerframework.checker.nullness.qual.NonNull;

import org.apache.commons.lang3.StringUtils;

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

    default Long getIdFromImageUri(@NonNull String imageUri) {

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
     * For the backend server this is e.g. https://poms-dev.omroep.nl/images/api/
     * for a frontend server this is e.g. https://images-dev.poms.omroep.nl/
     */
    String getImageBaseUrl();


    default String getOriginalUrl(@NonNull Long id) {
        String imageServerBaseUrl = getImageBaseUrl();
        StringBuilder result = new StringBuilder();
        result.append(imageServerBaseUrl).append(id);
        return result.toString();
    }

    default String getOriginalUrlFromImageUri(@NonNull  String imageUri) {
        return getOriginalUrl(getIdFromImageUri(imageUri));
    }

    /**
     * Resolves an web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    default String getImageLocation(@NonNull Long  id , String fileExtension, String... conversions) {
        String imageServerBaseUrl = getImageBaseUrl();
        StringBuilder builder = new StringBuilder(imageServerBaseUrl);
        for (String conversion : conversions) {
            builder.append(conversion);
            builder.append('/');
        }
        builder.append(id);
        if (fileExtension != null) {
            builder.append('.').append(fileExtension);
        }
        return builder.toString();

    }

}
