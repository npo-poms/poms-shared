package nl.vpro.domain.image;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.Beta;

import nl.vpro.jackson2.Jackson2Mapper;

@Beta
public interface Picture {

    Map<String, String> getSources();

    String getImageSrc();

    String getAlternative();

    String getStyle();

    @JsonIgnore
    default JsonNode getJson() {
        return Jackson2Mapper.getModelInstance().valueToTree(this);
    }

}
