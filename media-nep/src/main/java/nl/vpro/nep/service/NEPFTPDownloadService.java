package nl.vpro.nep.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Function;

public interface NEPFTPDownloadService {
    void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) throws IOException;

    default void download(String nepFile, OutputStream outputStream, Function<FileDescriptor, Boolean> descriptorConsumer) throws IOException {
        download(nepFile, outputStream, Duration.ZERO, descriptorConsumer);
    }
}
