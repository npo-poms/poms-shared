package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import nl.vpro.domain.image.ImageMetadataSupplier;
import nl.vpro.domain.image.backend.BackendImageSourceCreator;

/**
 * Wrapped for poms images.
 */
@Slf4j
public class PomsImages {


    public static class Creator extends BackendImageSourceCreator {

        @SuppressWarnings("unchecked")
        @Override
        protected Optional<Long> getId(ImageMetadataSupplier supplier) {
            if (supplier instanceof ImageMetadataSupplier.Wrapper) {
                Optional<Image> result = ((ImageMetadataSupplier.Wrapper) supplier).unwrap(Image.class);
                return result.map(Image::getImageId);
            } else {
                return Optional.empty();
            }
        }
    }
}
