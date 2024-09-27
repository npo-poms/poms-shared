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

    public static ProductRelations series(String mid) {
        return new ProductRelations(new PridHolder(mid), null, null);
    }

    public static ProductRelations season(String mid) {
        return new ProductRelations(null, new PridHolder(mid), null);
    }
}
