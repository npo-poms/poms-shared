package nl.vpro.domain.media.support;

import java.util.regex.Matcher;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface ImageBackendService {


    default Long getId(@Nonnull Image image) {

        return getIdFromImageUri(image.getImageUri());
    }

    default Long getIdFromImageUri(@Nonnull String imageUri) {

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

    String getImageBaseUrl();


    default String getOriginalUrl(Long id) {
        if (id == null) {
            return null;
        }
        String imageServerBaseUrl = getImageBaseUrl();
        StringBuilder result = new StringBuilder();
        result.append(imageServerBaseUrl).append("api/images/").append(id);
        return result.toString();
    }

    /**
     * Resolves an web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    default String getImageLocation(Long  id , String fileExtension, String... conversions) {
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
