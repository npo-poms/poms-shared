package nl.vpro.sourcingservice;


import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record Configuration(
    String baseUrl,
    @Nullable String callbackBaseUrl,
    @Nullable String callbackAuthentication,
    String token,
    int chunkSize,
    String defaultEmail) {

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
