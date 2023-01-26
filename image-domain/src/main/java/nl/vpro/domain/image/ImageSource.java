package nl.vpro.domain.image;

import lombok.*;

import java.io.Serializable;
import java.net.URI;
import java.util.Comparator;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import nl.vpro.domain.image.backend.BasicBackendImageMetadata;


/**
 * Representation of one image URL
 *
 * @see BasicBackendImageMetadata
 */
@With
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = ImageSource.Builder.class)
public class ImageSource implements Serializable {

    private static final long serialVersionUID = -7707025279370332657L;

    @ImageURL
    private final java.net.URI url;

    private final Type type;

    private final ImageFormat format;

    private final Dimension dimension;

    private final Area areaOfInterest;

    public static  ImageSource.Builder thumbNail(@ImageURL String url) {
        return  ImageSource.builder()
            .type(Type.THUMBNAIL)
            .url(url);
    }

    @lombok.Builder
    private  ImageSource(
        java.net.URI uri,
        Type type,
        ImageFormat format,
        Dimension dimension,
        Area areaOfInterest) {
        this.url = uri;
        this.type = type;
        this.format = format;
        this.dimension = dimension;
        this.areaOfInterest = areaOfInterest;
    }

    @Override
    public String toString() {
        return url + (dimension == null || dimension.getWidth() == null ? "" : " " + dimension.getWidth() +  "px");
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        public Builder url(@ImageURL String uri) {
            return uri(URI.create(uri));
        }
        public Builder key(Key key) {
            return type(key.getType())
                .format(key.getFormat());
        }

    }

    @JsonIgnore
    public Key getKey() {
        return new Key(type, format);
    }

    public enum Type {
        THUMBNAIL,

        MOBILE_HALF,
        MOBILE,
        MOBILE_2,
        MOBILE_3,

        TABLET,
        TABLET_2,
        TABLET_3,
        LARGE
    }

    @EqualsAndHashCode
    @Getter
    public static class Key implements Comparable<Key>, Serializable {
        private static final long serialVersionUID = 847885430222383460L;

        final Type type;
        final ImageFormat format;

        public static Key webp(Type type) {
            return new Key(type, ImageFormat.WEBP);
        }

        public static Key asis(Type type) {
            return new Key(type, ImageFormat.AS_IS);
        }

        public static Key jpeg(Type type) {
            return new Key(type, ImageFormat.JPG);
        }

        public Key(Type type, @Nullable ImageFormat format) {
            this.type = type;
            this.format = format;
        }
        @JsonCreator
        public Key(String key) {
            String[] split = key.split("_", 2);
            type = Type.valueOf(split[0]);
            format = split.length > 1 ? ImageFormat.valueOf(split[1]) : null;
        }


        @JsonValue
        public String name() {
            return (type.name() + (format == null ? "" : ("_" + format.name())));
        }

        @Override
        public String toString() {
            return name();
        }

        @Override
        public int compareTo(Key o) {
            int compare = type.compareTo(o.type);
            if (compare != 0) {
                return compare;
            }
            return Objects.compare(format, o.format, Comparator.nullsFirst(Comparator.naturalOrder()));
        }

    }
}
