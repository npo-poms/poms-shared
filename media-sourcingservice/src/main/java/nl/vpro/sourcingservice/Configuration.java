package nl.vpro.sourcingservice;

import org.checkerframework.checker.nullness.qual.Nullable;

public record Configuration(
    String baseUrl,
    @Nullable String callbackBaseUrl,
    @Nullable String callbackAuthentication,
    String token,
    int chunkSize,
    String defaultEmail) {

    public String cleanBaseUrl() {
        return  baseUrl.replaceAll("([^/])$","$1/");
    }

    public String callBackUrl(String mid) {
        if (callbackBaseUrl == null) {
            return null;
        } else {
            return callbackBaseUrl.formatted(mid);
        }
    }
}
