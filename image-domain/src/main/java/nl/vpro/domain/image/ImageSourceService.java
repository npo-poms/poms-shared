package nl.vpro.domain.image;

import java.util.*;

public class ImageSourceService {

    public static final ImageSourceService INSTANCE = new ImageSourceService();

    private final ServiceLoader<ImageSourceCreator> services = ServiceLoader.load(ImageSourceCreator.class);

    public <C extends ImageMetadataProvider> Map<ImageSource.Key, ImageSource> getSourceSet(C metadataProvider) {
        final SortedMap<ImageSource.Key, ImageSource> map = new TreeMap<>();
        services.forEach(creator -> {
            for (ImageSource.Type type : ImageSource.Type.values()) {
                creator.createFor(metadataProvider, type).ifPresent(imageSource -> {
                    map.put(imageSource.getKey(), imageSource);
                });
            }
        });
        return Collections.unmodifiableMap(map);
    }
}
