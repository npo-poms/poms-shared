package nl.vpro.berlijn.domain.productmetadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({
    "name" // code suffices
})
public record Genre(
    GenreType type,
    String code
) {
}
