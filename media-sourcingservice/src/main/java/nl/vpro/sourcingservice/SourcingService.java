package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;

public interface SourcingService {


     /**
     * Upload an (audio) asset to the NEP CDN
     * @param logger a logger to log progress to, may be showing to gui users too
     * @param mid the associated mid to upload the asset for
     * @param fileSize The Sourcing service needs to know beforehand how big the asset will be.
     * @param inputStream The inputStream for the asset. Will be implicitly closed when consumed (or when an exception occurs)
     * @param errors email address to associate with mishaps
     */
    CompletableFuture<UploadResponse> upload(
        SimpleLogger logger,
        String mid,
        long fileSize,
        String contentType,
        InputStream inputStream,
        @Nullable String profile,
        @Nullable String errors
    );



    /**
     * Who is calling {@link #status(String, Caller)}. Every caller has its own rate limiter, so that e.g. a background process polling for
     * statuses can't use up all calls, leaving nothing for users waiting in the GUI.
     * @since 8.19
     */
    enum Caller {
        /**
         * Background processes, like polling for statuses.
         */
        BACKGROUND,
        /**
         * Some user is waiting for the answer, e.g. in the GUI.
         */
        INTERACTIVE
    }

    /**
     * Retrieves the status of the asset for the given mid
     * @param caller determines which rate limiter is used
     * @return the status, or empty if the sourcing service doesn't know the mid
     * @throws RateLimitedException if the call was not done, because of rate limiting for the given caller
     * @since 8.19
     */
    Optional<StatusResponse> status(String mid, Caller caller) throws IOException, InterruptedException;

    /**
     * As {@link #status(String, Caller)}, for a {@link Caller#BACKGROUND background} caller.
     */
    default Optional<StatusResponse> status(String mid) throws IOException, InterruptedException {
        return status(mid, Caller.BACKGROUND);
    }


    DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException;


    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getUploadString();

    String name();


}
