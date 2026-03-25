package nl.vpro.domain.npo.wonvpp;

import jakarta.validation.constraints.NotNull;

public record GenreType(
    @NotNull String primary,
    @NotNull String secondary
) {

}
