package nl.vpro.sourcingservice;

import java.io.IOException;

/**
 * Thrown if a call to the sourcing service was not done, because the configured rate limit was exceeded.
 * @since 8.18
 */
public class RateLimitedException extends IOException {

    public RateLimitedException(String message) {
        super(message);
    }
}
