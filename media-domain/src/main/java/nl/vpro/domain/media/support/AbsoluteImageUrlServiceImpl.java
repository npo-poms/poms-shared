package nl.vpro.domain.media.support;


import javax.annotation.PostConstruct;
import javax.inject.Named;

/**
 * Implementation of {@link ImageUrlService} that can only be configured with an absolute base url.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class AbsoluteImageUrlServiceImpl implements ImageUrlService {

    protected final String imageServerBaseUrl;

    public AbsoluteImageUrlServiceImpl(
        @Named("image_frontend.baseUrl") String imageServerBaseUrl) {
        this.imageServerBaseUrl = imageServerBaseUrl;
    }

    @PostConstruct
    public void init() {
        ImageUrlServiceHolder.setInstance(this);
    }

    @Override
    public String getImageBaseUrl() {
        return imageServerBaseUrl;
    }

    @Override
    public String toString() {
        return imageServerBaseUrl + "<image>";
    }
}
