package nl.vpro.domain.image;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import nl.vpro.domain.convert.Conversions;

/**
 * Wrapper for the {@link ImageSourceCreator} {@link ServiceLoader}. This provides a service oriented way to create {@link ImageSourceSet}s from image metadata. Such a sourceset is not intrinsic to the image,
 * it may depend on the environment, and configuration how exactly it must be calculated.
 * <p>
 * This service provides the flexibility to do that.
 *
 * @since 7.2
 */
@Slf4j
public class ImageSourceService {

    public static final ImageSourceService INSTANCE = new ImageSourceService();

    @SuppressWarnings("rawtypes")
    private final ServiceLoader<ImageSourceCreator> services = ServiceLoader.load(ImageSourceCreator.class);

    /**
     * This is the simplest way use this service. Provide it with an instance of some class implementation {@link Metadata}.
     * <p>
     * The service loader needs to be aware of some {@link ImageSourceCreator} that supports this class.
     *
     */
    public ImageSourceSet getSourceSet(Metadata metadata) {
        return getSourceSet(metadata, metadata);
    }


    /**
     * Sometimes, for example for legacy reason, the image object itself cannot implement {@link Metadata}. It could then implement just {@link MetadataSupplier}. The associated {@link ImageSourceCreator}s can still receive the original image object.
     *
     */
    public ImageSourceSet getSourceSet(MetadataSupplier metadata) {
        return getSourceSet(metadata, metadata.getMetadata());
    }


    /**
     * Finally, it is possible to just pass both.
     */
    @SuppressWarnings("unchecked")
    public ImageSourceSet getSourceSet(Object sourceObject, Metadata metadata) {
        final Map<ImageSource.Key, ImageSource> map = new LinkedHashMap<>();
        final Set<String> set = new TreeSet<>();

        services.forEach(creator -> {
            try {
                for (ImageSource.Key key : Conversions.MAPPING.keySet()) {
                    log.debug("{}", key);
                    if (getTypeOf(creator).isAssignableFrom(sourceObject.getClass())) {
                        Optional<ImageSource> image = creator.createFor(sourceObject, metadata, key);
                        if (image.isPresent()) {
                            ImageSource source = image.get();
                            if (source.getDimension() == null) {
                                log.warn("Cannot add source without dimension {}", source);
                            } else {
                                if (set.add(source.getDimension() + "\t" + source.getFormat())) {
                                    map.put(image.get().getKey(), source);
                                } else {
                                    log.debug("Cannot add source {}, it has same existing dimension/format {}/{}", source, source.getDimension(), source.getFormat());
                                }
                            }
                        } else {
                            log.debug("No image could be created for {} by {}", key, creator);
                        }
                    } else {
                        log.debug("Creator {} doesn't support {}", creator, sourceObject.getClass());
                    }
                }
            } catch (Throwable e) {
                log.warn(e.getMessage());
            }
        });
        return new ImageSourceSet(map, ImageMetadata.of(metadata));
    }

    @SneakyThrows
    static Class<?> getTypeOf(ImageSourceCreator<?> creator)  {
        try {
            Type[] genericInterfaces = creator.getClass().getGenericInterfaces();
            if (genericInterfaces.length > 0) {
                return (Class<?>) ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];
            } else {
                return (Class<?>) ((ParameterizedType) creator.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            }
        } catch (ClassCastException cce) {
            log.warn(cce.getMessage());
            return Object.class;
        }

    }
}
