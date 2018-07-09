package nl.vpro.nep.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import nl.vpro.util.FileMetadata;

public interface NEPDownloadService {
    /**
     * Wait until the given file is available on the NEP download ftp server, then copy it to the given outputStream.
     *
     * Before that, you the descriptorConsumer will be called. It if return false, the copying will not happen
     */
    void download(
        @Nonnull String nepFile,
        @Nonnull Supplier<OutputStream> outputStream,
        @Nonnull Duration timeout,
        Function<FileMetadata, Boolean> descriptorConsumer) throws IOException;


    /**
     * Download the given file from the NEP ftp server to the given outputStream.
     */
    default void download(
        @Nonnull String nepFile,
        @Nonnull  Supplier<OutputStream> outputStream,
        @Nonnull Function<FileMetadata, Boolean> descriptorConsumer) throws IOException {
        download(nepFile, outputStream, Duration.ZERO, descriptorConsumer);
    }
}
