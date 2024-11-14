package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.i18n.MultiLanguageString.en;

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


    /**
     * a Consumer for {@link FileCachingInputStream} which logs progress to the logger, interpreting the inputstream as 'receive'.
     */
    static Consumer<FileCachingInputStream> loggingConsumer(final SimpleLogger logger, String impl) {
        return fci -> {
            if (fci.isReady()) {
                if (fci.getException().isEmpty()) {
                    logger.info(en("Received {} {}")
                        .nl("Ontvangen {} {}")
                        .slf4jArgs(impl, FileSizeFormatter.DEFAULT.format(fci.getCount())));
                } else {
                    logger.warn(en("Upload error ({}): {}")
                        .nl("Upload fout ({}) : {}")
                        .slf4jArgs(impl, fci.getException().get().getMessage())
                    );
                }
            }
        };
    }

}
