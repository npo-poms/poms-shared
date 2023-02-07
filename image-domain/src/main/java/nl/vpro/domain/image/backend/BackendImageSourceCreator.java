package nl.vpro.domain.image.backend;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import nl.vpro.domain.DomainObject;
import nl.vpro.domain.convert.Conversions;
import nl.vpro.domain.image.*;



/**
 * Wrapped for poms images.
 * @since 7.2
 */
@Slf4j
public class BackendImageSourceCreator implements ImageSourceCreator {

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Optional<Long> getId(Metadata supplier) {
        if (supplier instanceof ImageMetadata.Wrapper) {
            Optional<BackendImage> result = ((ImageMetadata.Wrapper) supplier).unwrap(BackendImage.class);
            return result.map(DomainObject::getId);
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ImageFormat getOriginalFormat(Metadata supplier) {
        if (supplier instanceof ImageMetadata.Wrapper) {
            Optional<BackendImage> result = ((ImageMetadata.Wrapper) supplier).unwrap(BackendImage.class);
            return result.map(BackendImage::getImageFormat).orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public Optional<ImageSource> createFor(Metadata supplier, ImageSource.Key key) {
        return getId(supplier)
            .map(id -> {
                final String[] transformation = Conversions.MAPPING.get(key);
                Dimension existingDimension = supplier.getDimension();
                final Dimension finalDim = Conversions.predictDimensions(existingDimension, transformation);
                ImageFormat finalFormat = key.getFormat() == null ? getOriginalFormat(supplier) : key.getFormat();
                return ImageSource.builder()
                    .type(key.getType())
                    .format(finalFormat)
                    .url(
                        ImageUrlServiceHolder.getInstance().getImageLocation(
                            id,
                            ImageFormat.getFileExtension(key.getFormat()),
                            true,
                                transformation
                        )
                    )
                    .dimension(finalDim)
                    .build();
                }
            );
    }

}
