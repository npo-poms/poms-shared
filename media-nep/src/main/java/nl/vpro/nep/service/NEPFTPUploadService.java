package nl.vpro.nep.service;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public interface NEPFTPUploadService {
    CompletableFuture<?> upload(String nepFile, InputStream stream);
}
