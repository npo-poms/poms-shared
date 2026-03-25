package nl.vpro.domain.npo.wonvpp;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SynopsisType(
    @JsonProperty("short") String shortValue,
    @JsonProperty("long") String longValue
) {
}
