package nl.npo.wonvpp.domain;

import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public record GenreType(
    @NotNull SubGenreType primary,
    @Nullable SubGenreType secondary
) {


    public boolean empty() {
        return primary == null || StringUtils.isBlank(primary.code());
    }
}
