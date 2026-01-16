package nl.vpro.domain.image;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import nl.vpro.domain.image.backend.BasicBackendImageMetadata;
import nl.vpro.jackson.Views;

import static nl.vpro.domain.image.ImageFormat.*;


/**
 * Representation of one image URL, especially in a {@link ImageSourceSet}.
 *
 * @see BasicBackendImageMetadata
 */
@With
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = ImageSource.Builder.class)
public class ImageSource implements Serializable, Comparable<ImageSource> {

    @Serial
    private static final long serialVersionUID = -7707025279370332657L;

    @ImageURL
    private final java.net.URI url;

    private final Type type;

    private final ImageFormat format;

    private final Dimension dimension;

    @JsonView(Views.Normal.class)
    private final Area areaOfInterest;

    public static  ImageSource.Builder thumbNail(@ImageURL String url) {
        return ImageSource.builder()
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

    /**
     * The point of interest is just the exact middle point of {@link #getAreaOfInterest()},
     * or if there is no such a thing is defined, then {@link RelativePoint#MIDDLE}
     */
    @JsonIgnore
    public RelativePoint getPointOfInterest() {
        return Area.relativeCenter(getAreaOfInterest(), getDimension());
    }

    @Override
    public String toString() {
        return url + (dimension == null || dimension.getWidth() == null ? "" : " " + dimension.getWidth() +  "px");
    }

    public int equivalent(ImageSource imageSource) {
        return Comparator.comparing(ImageSource::getType)
            .thenComparing(ImageSource::getDimension, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(ImageSource::getFormat, Comparator.nullsFirst(Comparator.naturalOrder()))
            .compare(this, imageSource);
    }

    @Override
    public int compareTo(ImageSource imageSource) {
        return equivalent(imageSource);
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

    public static Builder of(Key key) {
        return builder().key(key);
    }

    @JsonIgnore
    public Key getKey() {
        return new Key(type, format);
    }

    private static final Map<String, Type> lookup = new HashMap<>();
    public enum Type {

        THUMBNAIL("TN"),

        MOBILE_HALF("M0"),
        MOBILE("M1"),
        MOBILE_2("M2"),
        MOBILE_3("M3"),

        TABLET("T1"),
        TABLET_2("T2"),
        TABLET_3("T3"),

        LARGE("L1");


        @Getter
        private final String shortName;

        Type(String shortName) {
            this.shortName = shortName;
            if (lookup.put(shortName.toUpperCase(), this) != null) {
                throw new IllegalStateException();
            }
        }

        public static Type forShortName(String s) {
            Type t = lookup.get(s.toUpperCase());
            if (t == null) {
                throw new IllegalArgumentException();
            }
            return t;
        }
    }

    /**
     * Insided a {@link ImageSourceSet} every {@link ImageSource} has a unique key, which is a
     * combination of its {@link Type} and {@link ImageFormat}.
     */
    @EqualsAndHashCode
    @Getter
    public static class Key implements Comparable<Key>, Serializable {
        @Serial
        private static final long serialVersionUID = 847885430222383460L;

        final Type type;
        final ImageFormat format;

        public static Key webp(Type type) {
            return new Key(type, WEBP);
        }

        public static Key asis(Type type) {
            return new Key(type, AS_IS);
        }

        public static Key jpeg(Type type) {
            return new Key(type, JPG);
        }

        public Key(Type type, @Nullable ImageFormat format) {
            this.type = type;
            this.format = format;
        }
        @JsonCreator
        public Key(String key) {
            int lastU = key.lastIndexOf('.');
            if (lastU > 0) {
                type = Type.valueOf(key.substring(0, lastU));
                format = ImageFormat.valueOf(key.substring(lastU + 1));
            } else {
                type = Type.valueOf(key);
                format = null;
            }
        }


        @JsonValue
        public String name() {
            return (type.name() + (format == null ? "" : ("." + format.name())));
        }

        public String getShortName() {
            return type.getShortName() + (format == null ? "" : ("." + format.getShortName()));
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
