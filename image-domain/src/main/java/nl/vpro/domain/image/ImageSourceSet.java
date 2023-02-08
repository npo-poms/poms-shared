package nl.vpro.domain.image;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a set of {@link ImageSource}s
 */
@JsonSerialize
public class ImageSourceSet extends AbstractMap<ImageSource.Key, ImageSource> {

    final Map<ImageSource.Key, ImageSource> imageSources;

    private final ImageMetadata parent;

    ImageSourceSet(Map<ImageSource.Key, ImageSource> imageSources, ImageMetadata parent) {
        this.imageSources = imageSources;
        this.parent = parent;
    }

    public ImageSourceSet(ImageMetadata wrapped) {
        this(new LinkedHashMap<>(), wrapped);
    }

    @JsonCreator
    ImageSourceSet(Map<ImageSource.Key, ImageSource> imageSources) {
        this.imageSources = imageSources;
        parent = null;
    }

    public ImageSource getDefaultImageSource() {
        List<ImageSource> nonwebp = imageSources
            .values()
            .stream()
            .filter(e -> e.getFormat() != ImageFormat.WEBP)
            .collect(Collectors.toList());
        if (nonwebp.size() > 0) {
            return nonwebp.get(nonwebp.size() - 1);
        } else {
            return null;
        }
    }
    @Override
    public Set<Entry<ImageSource.Key, ImageSource>> entrySet() {
        return imageSources.entrySet();
    }

    @Override
    public ImageSource put(ImageSource.Key type, ImageSource source) {
        return imageSources.put(type, source);
    }

    public ImageSource put(ImageSource.Type type, ImageSource source) {
        return put(new ImageSource.Key(type,null), source);
    }

    @Override
    public String toString() {
        return getSourceSrc(is -> true);
    }

    public String getSourceSrc(Predicate<ImageSource> predicate) {
        StringBuilder builder = new StringBuilder();
        for (ImageSource imageSource : imageSources.values()) {
            if (predicate.test(imageSource)) {
                appendSrc(builder, imageSource);
            }
        }
        return builder.toString();
    }

    static void appendSrc(StringBuilder builder, ImageSource imageSource) {
        if (imageSource.getDimension() != null && imageSource.getDimension().getWidth() != null) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(imageSource.getUrl()).append(' ').append(imageSource.getDimension().getWidth()).append("w");
        }
    }

    public String getSourceSrc(ImageFormat format) {
        return forFormat(format).toString();
    }

    public ImageSourceSet forFormat(ImageFormat format) {
        final Map<ImageSource.Key, ImageSource> imageSources = new LinkedHashMap<>();
        final int[] skipped = {0};
        this.imageSources.forEach((key, value) -> {
            if (value.getFormat() == format) {
                imageSources.put(key, value);
            } else {
                skipped[0]++;
            }
        });
        if (skipped[0] == 0) {
            return this;
        } else {
            return new ImageSourceSet(imageSources, parent);
        }
    }

    /**
     * Represents the metadata to build an HTML picture tag.
     */
    public Picture getPicture() {
        return new PictureImpl(getSources(), getDefaultImageSource(), parent);
    }

    /**
     * Represents the metadata to build an HTML picture tag.
     */
    public PictureMetadata getPictureMetadata() {
        return new PictureMetadata(getSources(), getDefaultImageSource(), parent);
    }

    private Map<String, String> getSources() {
        Map<ImageFormat, StringBuilder> enumMap = new LinkedHashMap<>();
        for (ImageSource imageSource : imageSources.values()) {
            StringBuilder builder = enumMap.computeIfAbsent(imageSource.getFormat(), (f) -> new StringBuilder());
            appendSrc(builder, imageSource);
        }
        Map<String, String> result = new LinkedHashMap<>();
        enumMap.forEach((key, value) -> result.put(key == null ? "" : key.getMimeType(), value.toString()));
        return result;
    }


}
