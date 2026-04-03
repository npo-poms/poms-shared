package nl.npo.wonvpp.domain;

import jakarta.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;

public record GenreType(
    @NotNull String primary,
    @Nullable String secondary
) {

}
