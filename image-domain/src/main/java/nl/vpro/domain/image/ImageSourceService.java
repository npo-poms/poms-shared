package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

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
    public Map<ImageSource.Key, ImageSource> getSourceSet(ImageMetadataSupplier metadataProvider) {
        final SortedMap<ImageSource.Key, ImageSource> map = new TreeMap<>();
        services.forEach(creator -> {
            for (ImageSource.Key key : ImageSource.Key.values()) {
                Optional<ImageSource> image = creator.createFor(metadataProvider, key);
                if (image.isPresent()) {
                    map.put(image.get().getKey(), image.get());
                } else {
                    log.debug("No image could be created for {} by {}", key, creator);
                };
            }
        });
        return Collections.unmodifiableMap(map);
    }
}
