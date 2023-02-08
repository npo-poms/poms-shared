package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import nl.vpro.domain.image.backend.PomsImageSourceCreator;

/**
 * Wrapped for poms images.
 */
@Slf4j
public class PomsImages {

    public static class Creator extends PomsImageSourceCreator<Image> {

        @Override
        protected Optional<Long> getId(Image metadata) {
            return Optional.of(metadata.getImageId());
        }
    }
}
