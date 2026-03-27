package nl.npo.wonvpp.domain;

import jakarta.validation.constraints.NotNull;

public record PridReference(
    @NotNull String prid
) {
}
