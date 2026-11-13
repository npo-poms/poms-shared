package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.Serial;

/**
 * Thrown if a call to the sourcing service was not done, because the configured rate limit was exceeded.
 * @since 8.18
 */
public class RateLimitedException extends IOException {

    @Serial
    private static final long serialVersionUID = 6255707243198868205L;

    public RateLimitedException(String message) {
        super(message);
    }
}
