package nl.vpro.berlijn.domain.productmetadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"name"})
public record NicamContent(char code) {
}
