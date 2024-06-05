package nl.vpro.nep.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.logging.simple.SimpleLogger;

/**
 * Upload VIDEO to NEP.
 */
public interface NEPUploadService {


    /**
     * Upload (video) to NEP
     */
    long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull Path stream,
        boolean replaces
        ) throws IOException;

     long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull InputStream stream,
        boolean replaces
        ) throws IOException;


    String getUploadString();

}
