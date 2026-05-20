package nl.npo.wonvpp.domain;

import jakarta.validation.constraints.NotNull;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

public record SubGenreType(
    @NotNull String code,
    @Nullable String name
) {

    @JsonCreator
    public SubGenreType(String name) {
        this(null, name);
    }

}
