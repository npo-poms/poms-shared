package nl.vpro.nep.service;

import java.io.IOException;
import java.util.concurrent.Future;

public interface NEPFTPDownloadService {
    Future<?> download(String nepFile, Runnable... callbacks) throws IOException;
}
