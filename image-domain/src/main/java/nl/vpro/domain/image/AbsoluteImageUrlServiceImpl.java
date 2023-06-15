package nl.vpro.domain.image;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Implementation of {@link ImageUrlService} that can only be configured with an absolute base url.
 * @author Michiel Meeuwissen
 * @since 7.2
 */
public class AbsoluteImageUrlServiceImpl implements ImageUrlService {

    protected final String imageServerBaseUrl;

    @Inject
    public AbsoluteImageUrlServiceImpl(
        @Named("npo-images.baseUrl") String imageServerBaseUrl) {
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
