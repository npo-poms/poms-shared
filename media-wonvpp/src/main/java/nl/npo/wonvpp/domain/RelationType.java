package nl.npo.wonvpp.domain;

import jakarta.annotation.Nullable;

public record RelationType(
    @Nullable PridReference series,
    @Nullable PridReference season
) {
}
