package nl.vpro.domain.media.support;


import javax.annotation.PostConstruct;

/**
 * Implementation of {@link ImageUrlService} that can only be configured with an absolute base url.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class AbsoluteImageUrlServiceImpl implements ImageUrlService {

    protected final String imageServerBaseUrl;

    public AbsoluteImageUrlServiceImpl(String imageServerBaseUrl) {
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
