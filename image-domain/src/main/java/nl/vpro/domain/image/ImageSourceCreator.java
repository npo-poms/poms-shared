package nl.vpro.domain.image;

import java.util.Optional;

/**
 * A SPI to provide {@link ImageSource}s for a given {@link Metadata}.
 */
@FunctionalInterface
public interface ImageSourceCreator {

    /**
     * Given a source for image meta and a desired 'type' try to create {@link ImageSource} (basically a URL) for it.
     */
    Optional<ImageSource> createFor(
        Metadata imageMetadata,
        ImageSource.Key type
    );

}
