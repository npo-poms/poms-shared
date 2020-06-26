package nl.vpro.nep.service;

import java.io.IOException;
import java.io.InputStream;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.logging.simple.SimpleLogger;

public interface NEPUploadService {
    long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull InputStream stream,
        boolean replaces
        ) throws IOException;


    String getUploadString();

}
