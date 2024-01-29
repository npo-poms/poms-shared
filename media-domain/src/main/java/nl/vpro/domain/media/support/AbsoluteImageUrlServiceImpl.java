package nl.vpro.domain.media.support;


import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Implementation of {@link ImageUrlService} that can only be configured with an absolute base url.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class AbsoluteImageUrlServiceImpl extends nl.vpro.domain.image.AbsoluteImageUrlServiceImpl implements ImageUrlService {

    @Inject
    public AbsoluteImageUrlServiceImpl(
        @Named("npo-images.baseUrl") String imageServerBaseUrl) {
        super(imageServerBaseUrl);
    }

}
