package nl.vpro.nep.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Future;

public interface NEPFTPUploadService {
    Future<?> upload(String nepFile, InputStream stream, Runnable... callbacks) throws IOException;
}
