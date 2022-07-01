package nl.vpro.domain.image;

import lombok.*;

import java.net.URI;

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
@ToString
@EqualsAndHashCode
@JsonDeserialize(builder = ImageSource.Builder.class)
public class ImageSource {

    @ImageURL
    private final java.net.URI url;

    private final Type type;

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
        Dimension dimension,
        Area areaOfInterest) {
        this.url = uri;
        this.type = type;
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

    }

    public enum Type {
        THUMBNAIL,
        MOBILE,
        TABLET,
        LARGE
    }
}
