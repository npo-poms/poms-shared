package nl.vpro.domain.image;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * A {@link Picture} that also is {@link ImageMetadata}, so represents all available metadata for the image, and also
 * some (probably derivative) information for being a {@link Picture}
 * @since 7.2.3
 */
@Beta
@Getter
public class PictureMetadata extends MetadataWrapper implements Picture, ImageMetadata {

    private final Map<String, String> sources;
    final String imageSrc;
    @JsonIgnore
    protected ImageMetadata wrapped;

    String imageTitle;

    String alternative;

    public PictureMetadata(Map<String, String> sources, ImageSource image, @NonNull ImageMetadata wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
        this.sources = Collections.unmodifiableMap(sources);
        this.imageSrc = image == null || image.getUrl() == null ? null : image.getUrl().toString();
        this.imageTitle = wrapped.getTitle();
        this.alternative = wrapped.getAlternativeOrTitle();
    }

    @JsonProperty("pointOfInterest")
    public String getPointOfInterestAsString() {
        return wrapped.getPointOfInterest().toString();
    }

    @Override
    @JsonIgnore
    public ImageSourceSet getSourceSet() {
        return wrapped.getSourceSet();
    }

    @Override
    @JsonIgnore
    public @Nullable Area getAreaOfInterest() {
        return wrapped.getAreaOfInterest();
    }

    private static final ObjectMapper MAPPER = Jackson2Mapper.getInstance();

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
