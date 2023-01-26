package nl.vpro.domain.image;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ImageSourceSet extends AbstractMap<ImageSource.Key, ImageSource> {

    final Map<ImageSource.Key, ImageSource> imageSources;

    ImageSourceSet(Map<ImageSource.Key, ImageSource> imageSources) {
        this.imageSources = imageSources;
    }

    public ImageSourceSet() {
        this(new LinkedHashMap<>());
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
            return new ImageSourceSet(imageSources);
        }
    }

    public Map<ImageFormat, String> getSourceSrcs() {
        Map<ImageFormat, StringBuilder> enumMap = new LinkedHashMap<>();
        for (ImageSource imageSource : imageSources.values()) {
            StringBuilder builder = enumMap.computeIfAbsent(imageSource.getFormat(), (f) -> new StringBuilder());
            appendSrc(builder, imageSource);
        }
        Map<ImageFormat, String> result = new LinkedHashMap<>();
        enumMap.forEach((key, value) -> result.put(key, value.toString()));
        return result;
    }

}
