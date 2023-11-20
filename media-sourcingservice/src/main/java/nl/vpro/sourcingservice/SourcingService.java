package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.sourcingservice.v1.DeleteResponse;
import nl.vpro.util.FileCachingInputStream;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.i18n.MultiLanguageString.en;

public interface SourcingService {

    /**
     * Uploading to the sourcing service goes in these three phases.
     * @deprecated Phases existed only in version 1
     */
    @Deprecated
    enum Phase {
        START,
        UPLOAD,
        FINISH
    }




    /**
     * Upload an (audio) asset to the NEP CDN
     * @param logger a logger to log progress to, may be showing to gui users too
     * @param mid the associated mid to upload the asset for
     * @param restrictions (geo) restriction which may apply to this asset.
     * @param fileSize The Sourcing service needs to know beforehand how big the asset will be.
     * @param inputStream The inputStream for the asset. Will be implicitly closed when consumed (or when an exception occurs)
     * @param errors email address to associate with mishaps
     * @param phase a consumer which will be called when the {@link Phase 'phase'} of the upload process changes. Uploading to sourcing service ia a multistep process.
     */
    @Deprecated
    default UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        byte @Nullable[] checksum,
        InputStream inputStream,
        @Nullable String errors,
        Consumer<Phase> phase) throws IOException, InterruptedException, SourcingServiceException {
        return upload(logger, mid, restrictions, fileSize, inputStream, errors);
    }

    /**
     * Defaulting version of {@link #upload(SimpleLogger, String, Restrictions, long, byte[], InputStream, String, Consumer<Phase>)}.
     */
    UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors
    ) throws IOException, InterruptedException, SourcingServiceException;



    Optional<StatusResponse> status(String mid) throws IOException, InterruptedException;


    DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException;


    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getUploadString();


    /**
     * a Consumer for {@link FileCachingInputStream} which logs progress to the logger, interpreting the inputstream as 'upload'.
     */
    static Consumer<FileCachingInputStream> loggingConsumer(final SimpleLogger logger) {
        return fci -> {
            if (fci.isReady()) {
                if (fci.getException().isEmpty()) {
                    logger.info(en("Uploading ready ({} bytes)")
                        .nl("Uploaden klaar ({} bytes)")
                        .slf4jArgs(FileSizeFormatter.DEFAULT.format(fci.getCount())).build());
                } else {
                    logger.warn(en("Upload error: {}")
                        .nl("Upload fout: {}")
                        .slf4jArgs(fci.getException().get().getMessage()).build());
                }
            }
        };
    }

    /**
     * phases are veriosn 1
     */
    @Deprecated
    static Consumer<Phase> phaseLogger(final SimpleLogger logger) {
        return phase -> {
            logger.info(en("Phase {}")
                .nl("Fase {}")
                .slf4jArgs(phase).build());
        };
    }

}
