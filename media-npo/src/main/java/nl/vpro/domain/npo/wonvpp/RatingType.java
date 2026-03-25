package nl.vpro.domain.npo.wonvpp;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RatingType(
    @NotNull RatingSystem system,
    @NotNull String age,
    @NotNull List<@Size(min= 1, max=1) Map<AdvisoryType, @NotNull String>> advisories,
    @Nullable String pictograms
) {
}
