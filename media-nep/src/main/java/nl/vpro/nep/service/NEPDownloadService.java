package nl.vpro.nep.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Function;

import nl.vpro.util.FileMetadata;

public interface NEPDownloadService {
    void download(
        String nepFile,
        OutputStream outputStream,
        Duration timeout,
        Function<FileMetadata, Boolean> descriptorConsumer) throws IOException;

    default void download(
        String nepFile,
        OutputStream outputStream,
        Function<FileMetadata, Boolean> descriptorConsumer) throws IOException {
        download(nepFile, outputStream, Duration.ZERO, descriptorConsumer);
    }
}
