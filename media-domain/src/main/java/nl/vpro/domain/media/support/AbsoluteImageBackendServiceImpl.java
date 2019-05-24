package nl.vpro.domain.media.support;


import javax.annotation.PostConstruct;

/**
 * Implementation of {@link ImageBackendService} that can only be configured with an absolute base url.
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class AbsoluteImageBackendServiceImpl implements ImageBackendService {

    protected final String imageServerBaseUrl;

    public AbsoluteImageBackendServiceImpl(String imageServerBaseUrl) {
        this.imageServerBaseUrl = imageServerBaseUrl;
    }


    @PostConstruct
    public void init() {
        ImageBackendServiceHolder.setInstance(this);
    }

    @Override
    public String getImageBaseUrl() {
        return imageServerBaseUrl;

    }
}
