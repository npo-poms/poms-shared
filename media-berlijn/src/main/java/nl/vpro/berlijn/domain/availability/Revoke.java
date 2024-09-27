package nl.vpro.berlijn.domain.availability;

import java.time.Instant;

public record Revoke(
    boolean revoked,
    String transmissionId,
    Instant revokedTime
) {
}
