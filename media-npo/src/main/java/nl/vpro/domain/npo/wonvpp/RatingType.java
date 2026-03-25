package nl.vpro.domain.npo.wonvpp;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record RatingType(
    @NotNull String system,
    @NotNull String age,
    @NotNull List<AdvisoryType> advisories,
    @Nullable String pictograms
) {
}
