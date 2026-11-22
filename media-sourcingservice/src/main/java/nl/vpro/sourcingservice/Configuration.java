package nl.vpro.sourcingservice;


import java.time.Duration;

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
    long statusAllowedPerWindow,
    @Nullable Duration statusWindow,
    int bucketCount,
    long statusMaxPerBucket) {

    /**
     * Maximal time to wait for a permit for a status call, before giving up with a {@link RateLimitedException}.
     */
    public static final Duration STATUS_MAX_WAIT = Duration.ofSeconds(1);

    /**
     * Default for {@link #statusWindow()}
     */
    public static final Duration DEFAULT_STATUS_WINDOW = Duration.ofSeconds(10);

    /**
     * @param statusAllowedPerWindow the maximal number of status calls per {@code statusWindow}. Zero or negative means 'unlimited'.
     * @param statusWindow the (sliding) window for {@code statusAllowedPerWindow}. Defaults to {@link #DEFAULT_STATUS_WINDOW}
     * @param statusMaxPerBucket the maximal number of status calls per bucket (the window is divided in 20 buckets), to avoid bursts. Zero or negative means 'unlimited'.
     */
    public Configuration {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunkSize must be positive");
        }
        if (statusWindow == null) {
            statusWindow = DEFAULT_STATUS_WINDOW;
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
