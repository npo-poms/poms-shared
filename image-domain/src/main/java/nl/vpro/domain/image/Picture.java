package nl.vpro.domain.image;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.Beta;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * A 'picture' view on {@link ImageMetadata}. Mainly target at filling HTML picture elements.
 * <p>
 * It contains metadata relevant to the image itself, which could depend on what image actually is presented.

 */
@Beta
public interface Picture {

    @Data
    class Source {
        final String type;
        final String srcSet;

        public Source(String type, String srcSet) {
            this.type = type;
            this.srcSet = srcSet;
        }
    }

    /**
     * The sources of this picture. Keys are the type, values are srcset values. A list of urls with indicators when to use.
     */
    //@JsonIgnore
    Map<String, String> getSources();

    @JsonProperty("sources")
    @JsonIgnore
    default List<Source> getSourcesList() {
        return getSources().entrySet().stream().map(e -> new Source(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    /**
     * The default image URL to use. The src attribute value of the child 'img' tag element.
     */
    String getImageSrc();

    /**
     * An alternative for the image, for when it's not viewable. The alt attribute the img tag element.
     */
    String getAlternative();

    /**
     * The title of the image, as it should appear on the img child element.
     */
    String getImageTitle();

    /**
     * Width of the original, unscaled, image, to be used a 'with' attribute of the img element.
     */
    Integer getWidth();

    /**
     * Height of the original, unscaled, image, to be used a 'with' attribute of the img element.
     */
    Integer getHeight();


    /**
     * A JSON Presentation of this picture.
     */
    @JsonIgnore
    default JsonNode getJson() {
        return Jackson2Mapper.getModelInstance().valueToTree(this);
    }

}
