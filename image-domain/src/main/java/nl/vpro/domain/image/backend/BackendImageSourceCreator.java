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
    protected Optional<Long> getId(ImageMetadataSupplier supplier) {
        if (supplier instanceof ImageMetadataSupplier.Wrapper) {
            Optional<BackendImage> result = ((ImageMetadataSupplier.Wrapper) supplier).unwrap(BackendImage.class);
            return result.map(DomainObject::getId);
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected ImageFormat getOriginalFormat(ImageMetadataSupplier supplier) {
        if (supplier instanceof ImageMetadataSupplier.Wrapper) {
            Optional<BackendImage> result = ((ImageMetadataSupplier.Wrapper) supplier).unwrap(BackendImage.class);
            return result.map(BackendImage::getImageFormat).orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public Optional<ImageSource> createFor(ImageMetadataSupplier supplier, ImageSource.Key key) {
        final String[] transformation  = Conversions.MAPPING.get(key);
        Dimension existingDimension = supplier.getImageMetadata().getDimension();
        final Dimension finalDim = Conversions.predictDimensions(existingDimension, transformation);
        ImageFormat finalFormat = key.getFormat() == null ? getOriginalFormat(supplier) : key.getFormat();
        return getId(supplier)
            .map(id ->
                ImageSource.builder()
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
                    .build()
            );
    }

}
