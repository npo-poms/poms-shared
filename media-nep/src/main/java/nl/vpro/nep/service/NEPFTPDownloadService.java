package nl.vpro.nep.service;

import java.io.IOException;
import java.io.OutputStream;

public interface NEPFTPDownloadService {
    void download(String nepFile, OutputStream outputStream) throws IOException;
}
