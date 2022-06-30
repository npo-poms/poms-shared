package nl.vpro.domain.image;

import java.util.Optional;

/**
 * A SPI to provide {@link ImageSource}s for a given {@link ImageMetadataProvider}.
 */
public interface ImageSourceCreator {

    /**
     * Given a source for image meta and a desired 'type' try to create {@link ImageSource} (basically a URL) for it.
     */
    Optional<ImageSource> createFor(ImageMetadataProvider provider, ImageSource.Type type);
}
