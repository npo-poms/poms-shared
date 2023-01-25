package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import nl.vpro.domain.convert.Conversions;

/**
 * Wrapper for the {@link ImageSourceCreator} {@link ServiceLoader}.
 *
 */
@Slf4j
public class ImageSourceService {

    public static final ImageSourceService INSTANCE = new ImageSourceService();

    private final ServiceLoader<ImageSourceCreator> services = ServiceLoader.load(ImageSourceCreator.class);

    /**
     *
     */
    public ImageSourceSet getSourceSet(ImageMetadataSupplier metadataProvider) {
        final Map<ImageSource.Key, ImageSource> map = new LinkedHashMap<>();
        final Set<ImageSource> set = new HashSet<>();
        services.forEach(creator -> {
            for (ImageSource.Key key : Conversions.MAPPING.keySet()) {
                Optional<ImageSource> image = creator.createFor(metadataProvider, key);
                if (image.isPresent()) {
                    ImageSource source = image.get();
                    if(set.add(source)) {
                        map.put(image.get().getKey(), source);
                    }
                } else {
                    log.debug("No image could be created for {} by {}", key, creator);
                };
            }
        });
        return new ImageSourceSet(map);
    }
}
