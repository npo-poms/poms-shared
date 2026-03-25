package nl.vpro.domain.npo.wonvpp;

import jakarta.validation.constraints.NotNull;

public record PersonType(
    @NotNull String giveName,
    @NotNull String familyName,
    @NotNull Integer number
) {
}
