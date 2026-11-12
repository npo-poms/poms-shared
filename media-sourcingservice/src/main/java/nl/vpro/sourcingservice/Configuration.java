package nl.vpro.sourcingservice;


import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record Configuration(
    String baseUrl,
    @Nullable String callbackBaseUrl,
    @Nullable String callbackAuthentication,
    String token,
    int chunkSize,
    String defaultEmail,
    boolean useParameterAuthentication,
    double statusRateLimit) {

    /**
     * Maximal time to wait for a permit for a status call, before giving up with a {@link RateLimitedException}.
     */
    public static final java.time.Duration STATUS_MAX_WAIT = java.time.Duration.ofSeconds(1);

    /**
     * @param statusRateLimit the maximal number of status calls per second. Zero or negative means 'unlimited'.
     */
    public Configuration {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }
    }

    public String cleanBaseUrl() {
        return  baseUrl.replaceAll("([^/])$","$1/");
    }

    public @Nullable String callBackUrl(@NonNull String mid) {
        if (callbackBaseUrl == null) {
            return null;
        } else {
            return callbackBaseUrl.formatted(mid);
        }
    }
}
