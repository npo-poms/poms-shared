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


    String getOriginalUrl(Long id);

    /**
     * Resolves an web location for images. Relies on a system property #IMAGE_SERVER_BASE_URL_PROPERTY to
     * obtain a base url for an image host.
     *
     * @return valid url string or null if it can't resolve a location
     * @throws NullPointerException on null arguments or null imageUri
     */
    String getImageLocation(Long  id , String fileExtension, String... conversions);

}
