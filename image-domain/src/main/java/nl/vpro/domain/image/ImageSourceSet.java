package nl.vpro.domain.image;

import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ImageSourceSet extends AbstractMap<ImageSource.Type, ImageSource> {

    final Map<ImageSource.Type, ImageSource> imageSources = new TreeMap<>();

    @Override
    public Set<Entry<ImageSource.Type, ImageSource>> entrySet() {
        return imageSources.entrySet();
    }

    @Override
    public ImageSource put(ImageSource.Type type, ImageSource source) {
        return imageSources.put(type, source);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ImageSource imageSource : imageSources.values()) {
            if (imageSource.getDimension() != null && imageSource.getDimension().getWidth() != null) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(imageSource.getUrl()).append(" w").append(imageSource.getDimension().getWidth());
            }
        }
        return builder.toString();
    }
}
