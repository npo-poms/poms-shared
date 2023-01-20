package nl.vpro.domain.image;

import java.util.Optional;

/**
 * A SPI to provide {@link ImageSource}s for a given {@link ImageMetadataProvider}.
 */
public interface ImageSourceCreator {



    @Deprecated
    default Optional<ImageSource> createFor(ImageMetadataProvider provider, ImageSource.Type type) {
        return createFor(provider, new ImageSource.Key(type, null));
    }

    /**
     * Given a source for image meta and a desired 'type' try to create {@link ImageSource} (basically a URL) for it.
     */
    default Optional<ImageSource> createFor(ImageMetadataProvider provider, ImageSource.Key type) {
        return createFor(provider, type.getType());
    }
}
