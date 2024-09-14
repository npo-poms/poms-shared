package nl.vpro.nep.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.logging.simple.SimpleLogger;

public interface NEPUploadService {


    long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull Path stream,
        boolean replaces
        ) throws IOException;

    /**
     * Upload streamingly.
     *
     * See MSE-5800, this doesn't work with sshj anymore.
     */
     long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull InputStream stream,
        boolean replaces
        ) throws IOException;


    String getUploadString();

}
