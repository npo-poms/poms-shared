package nl.vpro.domain.npo.wonvpp;

import jakarta.validation.constraints.NotNull;

public record PridReference(
    @NotNull String prid
) {
}
