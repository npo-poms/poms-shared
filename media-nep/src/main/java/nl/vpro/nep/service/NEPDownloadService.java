package nl.vpro.nep.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import nl.vpro.util.FileMetadata;

public interface NEPDownloadService {
    void download(
        @Nonnull String nepFile,
        @Nonnull Supplier<OutputStream> outputStream,
        @Nonnull Duration timeout,
        Function<FileMetadata, Boolean> descriptorConsumer) throws IOException;

    default void download(
        @Nonnull String nepFile,
        @Nonnull  Supplier<OutputStream> outputStream,
        @Nonnull Function<FileMetadata, Boolean> descriptorConsumer) throws IOException {
        download(nepFile, outputStream, Duration.ZERO, descriptorConsumer);
    }
}
