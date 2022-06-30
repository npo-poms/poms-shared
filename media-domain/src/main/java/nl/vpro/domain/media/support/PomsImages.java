package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import nl.vpro.domain.image.*;

/**
 * Wrapped for poms images.
 */
@Slf4j
public class PomsImages {


    static Map<ImageSource.Type, String> MAPPING = new HashMap<>();
    static {
        MAPPING.put(ImageSource.Type.THUMBNAIL, "s100");
        MAPPING.put(ImageSource.Type.MOBILE, "s200");
        MAPPING.put(ImageSource.Type.TABLET, "s300");
        MAPPING.put(ImageSource.Type.LARGE, "s400");
    }

    public static class Creator implements ImageSourceCreator {

        @Override
        public Optional<ImageSource> createFor(ImageMetadataProvider provider, ImageSource.Type type) {
            if (provider instanceof ImageMetadataProvider.Wrapper) {
                ImageMetadataProvider.Wrapper<?> pomsImageMetadata = (ImageMetadataProvider.Wrapper) provider;
                final String transformation  = MAPPING.get(type);
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
                        .type(type)
                        .url(
                            ImageUrlServiceHolder.getInstance().getImageLocation(i.getId(), null, transformation))
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
