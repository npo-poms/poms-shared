package nl.vpro.nep.service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

public interface NEPFTPUploadService {
    CompletableFuture<?> upload(Logger logger, String nepFile, Long size, InputStream stream);
}
