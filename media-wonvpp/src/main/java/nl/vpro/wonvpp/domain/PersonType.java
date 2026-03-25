package nl.vpro.wonvpp.domain;

import jakarta.validation.constraints.NotNull;

public record PersonType(
    @NotNull String givenName,
    @NotNull String familyName,
    @NotNull Long id
) {
}
