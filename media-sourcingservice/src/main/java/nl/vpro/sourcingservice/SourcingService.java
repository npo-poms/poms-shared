package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;

public interface SourcingService {

    enum Phase {
        START,
        UPLOAD,
        FINISH
    }


    UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors,
        Consumer<Phase> phase) throws IOException, InterruptedException;

    default UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors
    ) throws IOException, InterruptedException {
        return upload(logger, mid, restrictions, fileSize, inputStream, errors, (p) -> {});
    }

    Optional<StatusResponse> status(String mid) throws IOException, InterruptedException;


    DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException;




    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getUploadString();

}
