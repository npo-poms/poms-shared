package nl.npo.wonvpp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SynopsisType(
    @JsonProperty("short") String shortValue,
    @JsonProperty("long") String longValue
) {
}
