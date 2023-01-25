package nl.vpro.domain.image;

import java.util.*;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ImageSourceSet extends AbstractMap<ImageSource.Key, ImageSource> {

    final Map<ImageSource.Key, ImageSource> imageSources;

    ImageSourceSet(Map<ImageSource.Key, ImageSource> imageSources) {
        this.imageSources = imageSources;
    }

    public ImageSourceSet() {
        this(new TreeMap<>());
    }

    public ImageSource getDefaultImageSource() {
        return imageSources.entrySet().stream().findFirst().map(Entry::getValue).orElse(null);

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
        final Map<ImageSource.Key, ImageSource> imageSources = new TreeMap<>();
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
            return new ImageSourceSet(imageSources);
        }
    }

    public EnumMap<ImageFormat, String> getSourceSrcs() {
        EnumMap<ImageFormat, StringBuilder> enumMap = new EnumMap<ImageFormat, StringBuilder>(ImageFormat.class);
        for (ImageSource imageSource : imageSources.values()) {
            StringBuilder builder = enumMap.computeIfAbsent(imageSource.getFormat(), (f) -> new StringBuilder());
            appendSrc(builder, imageSource);
        }
        EnumMap<ImageFormat, String> result = new EnumMap<ImageFormat, String>(ImageFormat.class);
        enumMap.forEach((key, value) -> result.put(key, value.toString()));
        return result;
    }

}
