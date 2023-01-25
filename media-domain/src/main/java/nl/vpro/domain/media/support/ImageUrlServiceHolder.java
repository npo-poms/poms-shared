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
}
