package nl.vpro.domain.image;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import nl.vpro.domain.convert.Conversions;

/**
 * Wrapper for the {@link ImageSourceCreator} {@link ServiceLoader}.
 *
 */
@Slf4j
public class ImageSourceService {

    public static final ImageSourceService INSTANCE = new ImageSourceService();

    @SuppressWarnings("rawtypes")
    private final ServiceLoader<ImageSourceCreator> services = ServiceLoader.load(ImageSourceCreator.class);


    public ImageSourceSet getSourceSet(MetadataSupplier metadata) {
        return getSourceSet(metadata, metadata.getMetadata());
    }


    public ImageSourceSet getSourceSet(Metadata metadata) {
        return getSourceSet(metadata, metadata);
    }

    /**
     */
    @SuppressWarnings("unchecked")
    protected ImageSourceSet getSourceSet(Object sourceObject, Metadata metadata) {
        final Map<ImageSource.Key, ImageSource> map = new LinkedHashMap<>();
        final Set<String> set = new TreeSet<>();

        services.forEach(creator -> {

            for (ImageSource.Key key : Conversions.MAPPING.keySet()) {
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
                    ;
                }
            }
        });
        return new ImageSourceSet(map, ImageMetadata.of(metadata));
    }

    @SneakyThrows
    public static Class<?> getTypeOf(ImageSourceCreator<?> creator)  {
        Type[] genericInterfaces = creator.getClass().getGenericInterfaces();
        if (genericInterfaces.length > 0) {
            return (Class<?>) ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];
        } else {
            return (Class<?>) ((ParameterizedType) creator.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }

    }
}
