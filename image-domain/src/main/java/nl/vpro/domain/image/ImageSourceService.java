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
    public ImageSourceSet getSourceSet(Metadata metadata) {
        final Map<ImageSource.Key, ImageSource> map = new LinkedHashMap<>();
        final Set<String> set = new TreeSet<>();

        services.forEach(creator -> {
            for (ImageSource.Key key : Conversions.MAPPING.keySet()) {
                Optional<ImageSource> image = creator.createFor(metadata, key);
                if (image.isPresent()) {
                    ImageSource source = image.get();
                    if(set.add(source.getDimension() + "\t" + source.getFormat())) {
                        map.put(image.get().getKey(), source);
                    }
                } else {
                    log.debug("No image could be created for {} by {}", key, creator);
                };
            }
        });
        return new ImageSourceSet(map, ImageMetadata.of(metadata));
    }
}
