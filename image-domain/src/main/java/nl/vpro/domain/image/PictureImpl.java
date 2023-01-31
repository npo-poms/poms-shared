package nl.vpro.domain.image;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;

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

    String style;

    String alternative;

    Integer width;

    Integer height;

    public PictureImpl(Map<String, String> sources, ImageSource image, @NonNull ImageMetadata wrapped) {
        this.wrapped = wrapped;
        this.sources = Collections.unmodifiableMap(sources);
        this.imageSrc = image.getUrl().toString();
        this.alternative = wrapped.getAlternativeOrTitle();
        this.width = Dimension.getIntegerWidth(wrapped.getDimension());
        this.height = Dimension.getIntegerHeight(wrapped.getDimension());
    }

    @JsonProperty("pointOfInterest")
    public String getPointOfInterestAsString() {
        if (wrapped.getPointOfInterest() == null){
            return "50% 50%";
        } else {
            return wrapped.getPointOfInterest().toString();
        }
    }

}
