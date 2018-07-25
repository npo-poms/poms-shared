package nl.vpro.nep.service;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import nl.vpro.logging.simple.SimpleLogger;

public interface NEPUploadService {
    long upload(
        @Nonnull SimpleLogger logger,
        @Nonnull String nepFile,
        @Nonnull Long size,
        @Nonnull InputStream stream) throws IOException;
}
