package nl.npo.wonvpp.domain;

import jakarta.validation.constraints.NotNull;

public record GenreType(
    @NotNull String primary,
    @NotNull String secondary
) {

}
