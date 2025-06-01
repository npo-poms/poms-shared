package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

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
    UploadResponse upload(
        SimpleLogger logger,
        String mid,
        long fileSize,
        String mimeType,
        InputStream inputStream,
        @Nullable String errors
    ) throws IOException, InterruptedException, SourcingServiceException;



    Optional<StatusResponse> status(String mid) throws IOException, InterruptedException;


    DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException;


    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getUploadString();


}
