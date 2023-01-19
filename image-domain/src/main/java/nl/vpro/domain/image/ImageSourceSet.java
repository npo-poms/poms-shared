package nl.vpro.domain.image;

import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ImageSourceSet extends AbstractMap<ImageSource.Key, ImageSource> {

    final Map<ImageSource.Key, ImageSource> imageSources = new TreeMap<>();

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
        StringBuilder builder = new StringBuilder();
        for (ImageSource imageSource : imageSources.values()) {
            if (imageSource.getDimension() != null && imageSource.getDimension().getWidth() != null) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(imageSource.getUrl()).append(' ').append(imageSource.getDimension().getWidth()).append("w");
            }
        }
        return builder.toString();
    }
}
