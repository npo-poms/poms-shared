package nl.vpro.sourcingservice;

import java.io.IOException;
import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;

public interface SourcingService {

    UploadResponse uploadAudio(
        SimpleLogger logger,
        String mid,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors) throws IOException, InterruptedException;


    UploadResponse uploadVideo(
        SimpleLogger logger,
        String mid,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors) throws IOException, InterruptedException;

    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getAudioUploadString();

    String getVideoUploadString();


}
