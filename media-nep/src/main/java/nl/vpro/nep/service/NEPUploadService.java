package nl.vpro.nep.service;

import lombok.With;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.mediainfo.MediaInfo;

public interface NEPUploadService {


    UploadResult upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull Path stream,
        boolean replaces
        ) throws IOException;

    /**
     * Upload streamingly.
     * <p>
     * See MSE-5800, this doesn't work with sshj anymore.
     * @deprecated
     */
    @Deprecated
    long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull InputStream stream,
        boolean replaces
        ) throws IOException;


    String getUploadString();

    record UploadResult(long size, String server, String error, @With MediaInfo mediaInfo) {

        public static UploadResult sizeOnly(long size, String server) {
            return new UploadResult(size, server, null, null);
        }
    }

}
