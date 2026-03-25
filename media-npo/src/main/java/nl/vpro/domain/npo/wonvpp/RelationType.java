package nl.vpro.domain.npo.wonvpp;

import jakarta.annotation.Nullable;

public record RelationType(
    @Nullable PridReference series,
    @Nullable PridReference season
) {
}
