package nl.vpro.nep.service;

import java.io.IOException;
import java.io.InputStream;

import nl.vpro.logging.simple.SimpleLogger;

public interface NEPFTPUploadService {
    long upload(SimpleLogger logger, String nepFile, Long size, InputStream stream) throws IOException;
}
