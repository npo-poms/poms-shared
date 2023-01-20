package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.meeuw.configuration.FixedSizeMap;

import nl.vpro.domain.image.*;

/**
 * Wrapped for poms images.
 */
@Slf4j
public class PomsImages {


    private static final Map<ImageSource.Type, String> MAPPING;
    static {
        Map<ImageSource.Type, String> mapping = new HashMap<>();
        mapping.put(ImageSource.Type.THUMBNAIL, "s100");
        mapping.put(ImageSource.Type.MOBILE, "s414");
        mapping.put(ImageSource.Type.TABLET, "s1024>");
        mapping.put(ImageSource.Type.LARGE, "s2048>");
        // TODO: FixedSizeMap 0.10 (not yet released) has nicer constructors.
        MAPPING = new FixedSizeMap<>(mapping);
    }

    public static class Creator implements ImageSourceCreator {


        @Override
        public Optional<ImageSource> createFor(ImageMetadataProvider provider, ImageSource.Key key) {
            if (provider instanceof ImageMetadataProvider.Wrapper) {
                ImageMetadataProvider.Wrapper<?> pomsImageMetadata = (ImageMetadataProvider.Wrapper<?>) provider;
                final String transformation  = MAPPING.get(key.getType());
                Dimension existingDimension = pomsImageMetadata.getWrapped().getDimension();
                Dimension dimension;
                try {
                    dimension = Dimension.of(Integer.parseInt(transformation.substring(1)), null);
                } catch (Exception e) {
                    log.warn("Couldn't parse dimension from {}", transformation);
                    dimension = null;
                }
                final Dimension finalDim = dimension;
                return pomsImageMetadata.unwrap(Image.class)
                    .map(i -> ImageSource.builder()
                        .type(key.getType())
                        .format(key.getFormat())
                        .url(
                            ImageUrlServiceHolder.getInstance().getImageLocation(
                                i.getImageUri(),
                                ImageFormat.getFileExtension(key.getFormat()), transformation)
                        )
                        .dimension(finalDim)
                        .build()
                    );

            } else {
                log.debug("Could not create for {}", provider);
            }
            return Optional.empty();
        }
    }
}
