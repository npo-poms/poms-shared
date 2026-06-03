package nl.vpro.berlijn.domain.productmetadata;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.JsonProperties;
import nl.vpro.domain.media.AgeRating;

public enum NicamAge implements JsonProperties {

    @JsonProperty("6")
    _6,
    @JsonProperty("9")
    _9,
    @JsonProperty("12")
    _12,
    @JsonProperty("14")
    _14,
    @JsonProperty("16")
    _16,
    @JsonProperty("18")
    _18,

    @JsonProperty("AL")
    ALL;

    public AgeRating toAgeRating() {
        return AgeRating.valueOf(name());
    }

}
