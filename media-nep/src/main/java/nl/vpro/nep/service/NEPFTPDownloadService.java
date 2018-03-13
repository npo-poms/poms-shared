package nl.vpro.nep.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface NEPFTPDownloadService {
    CompletableFuture<?> download(String nepFile) throws IOException;
}
