package nl.vpro.domain.image;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @since 7.2.3
 */
@Beta
@Getter
public class PictureImpl implements Picture {

    private final Map<String, String> sources;
    final String imageSrc;
    @JsonIgnore
    protected ImageMetadata wrapped;


    String alternative;

    String imageTitle;

    Integer width;

    Integer height;


    public PictureImpl(
        Map<String, String> sources,
        ImageSource image, @NonNull ImageMetadata wrapped) {
        this.wrapped = wrapped;
        this.sources = Collections.unmodifiableMap(sources);
        this.imageSrc = image == null || image.getUrl() == null ? null : image.getUrl().toString();
        this.alternative = wrapped.getAlternativeOrTitle();
        this.imageTitle = wrapped.getTitle();
        this.width = Dimension.getIntegerWidth(wrapped.getDimension());
        this.height = Dimension.getIntegerHeight(wrapped.getDimension());
    }

    @JsonProperty("pointOfInterest")
    public String getPointOfInterestAsString() {
        return wrapped.getPointOfInterest().toString();
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
