package nl.vpro.berlijn.domain.productmetadata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Two prid-holders.
 */
@JsonIgnoreProperties({"episodes"})
public record ProductRelations(
    PridHolder series,
    PridHolder season,
    List<String> seasons
) {
}
