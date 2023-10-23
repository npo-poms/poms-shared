package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;

public interface SourcingService {

    /**
     * Uploading to the sourcing service goes in these three phases.
     */
    enum Phase {
        START,
        UPLOAD,
        FINISH
    }


    /**
     * Upload an (audio) asset to the NEP CDN
     * @param logger a logger to log progress to, may be showing to gui users too
     * @param mid the associated mid to upload the asset for
     * @param restrictions (geo) restriction may apply to this asset.
     * @param fileSize The Sourcing service needs to know beforehand how big the asset will be.
     * @param inputStream The inputStream for the asset. Will be implicitly closed when consumed (or when an exception occurs)
     * @param errors email address to associate with mishaps
     * @param phase a consumer which will be called when the {@link Phase 'phase'} of the upload process changes. Uploading to sourcing service ia a multistep process.
     */
    UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors,
        Consumer<Phase> phase) throws IOException, InterruptedException, SourcingServiceException;

    default UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors
    ) throws IOException, InterruptedException, SourcingServiceException {
        return upload(logger, mid, restrictions, fileSize, inputStream, errors, (p) -> {});
    }

    Optional<StatusResponse> status(String mid) throws IOException, InterruptedException;


    DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException;




    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getUploadString();

}
