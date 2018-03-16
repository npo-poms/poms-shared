package nl.vpro.nep.service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import nl.vpro.logging.SimpleLogger;

public interface NEPFTPUploadService {
    CompletableFuture<?> upload(SimpleLogger logger, String nepFile, Long size, InputStream stream);
}
