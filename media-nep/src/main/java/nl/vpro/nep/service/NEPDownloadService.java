package nl.vpro.nep.service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.util.FileMetadata;

public interface NEPDownloadService {
    /**
     * Wait until the given file is available on the NEP download ftp server, then copy it to the given outputStream.
     *
     * Before that, the descriptorConsumer will be called. If it returns false, the copying will not happen
     *
     * @throws IllegalStateException If the file didn't appear in time
     * @param descriptorConsumer If the file is found, and this is not <code>null</code> it's descriptor will be fed to this function
     *                           You can use it for logging only and return {@link Proceed#TRUE} always. You can also return something else.
     */
    void download(
        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout,
        @Nullable Function<FileMetadata, Proceed> descriptorConsumer) throws IOException;

    /**
     * Download the given file from the NEP ftp server to the given outputStream.
     */
    default void download(
        @NonNull String nepFile,
        @NonNull  Supplier<OutputStream> outputStream,
        @NonNull Function<FileMetadata, Proceed> descriptorConsumer) throws IOException {
        download(nepFile, outputStream, Duration.ZERO, descriptorConsumer);
    }

    enum Proceed {
        /**
         * The file is ok, simply proceed downloading it.
         */
        TRUE,
        /**
         * The file is not ok, it may be too old or different than expected. Wait a bit and retry this.
         */
        RETRY,
        /**
         * The file is not ok, it may be too old or different than expected. There is no point in waiting.
         */
        FALSE
    }
}
