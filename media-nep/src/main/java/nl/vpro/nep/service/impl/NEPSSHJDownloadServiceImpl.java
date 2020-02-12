package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.FileMetadata;


/**
 * This is a wrapper for sftp-itemizer.nepworldwide.nl This is were itemize results are placed by NEP
 *
 * Not used, because of https://github.com/hierynomus/sshj/issues/432
 */
//@Named("NEPDownloadService") // It doesn't *** work
@Slf4j
public class NEPSSHJDownloadServiceImpl implements NEPDownloadService {

    private final String ftpHost;
    private final String username;
    private final String password;
    private final String hostKey;

    @Inject
    public NEPSSHJDownloadServiceImpl(
        @Value("${nep.itemizer-download.host}") String ftpHost,
        @Value("${nep.itemizer-download.username}") String username,
        @Value("${nep.itemizer-download.password}") String password,
        @Value("${nep.itemizer-download.hostkey}") String hostKey
    ) {
        this.ftpHost = ftpHost;
        this.username = username;
        this.password = password;
        this.hostKey = hostKey;
    }

    @PostConstruct
    public void init() {
        log.info("NEP download service for {}@{}", username, ftpHost);
    }

    @Override
    public void download(
        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout, Function<FileMetadata, Proceed> descriptorConsumer) {
        log.info("Started nep file transfer service for {}@{} (hostkey: {})", username, ftpHost, hostKey);
        if (StringUtils.isBlank(nepFile)) {
            throw new IllegalArgumentException();
        }
        try {
            checkAvailabilityAndConsume(nepFile, timeout, descriptorConsumer, (handle) -> {
                OutputStream out = null;
                try {
                    out = outputStream.get();
                    if (out != null) {
                        try (InputStream in = handle.new ReadAheadRemoteFileInputStream(32)) {
                            long copy = IOUtils.copy(in, out, 1024 * 10);
                            log.info("Copied {} bytes", copy);
                        } catch (SFTPException sfte) {
                            log.error(sfte.getMessage());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ioe) {
                            log.warn("ioe.");
                        }
                    }

                }
                }
            );
        } catch (IOException  e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();

        }
    }

    protected void checkAvailabilityAndConsume   (
        @NonNull String nepFile,
        @Nullable Duration timeout,
        @Nullable Function<FileMetadata, Proceed> descriptorConsumer,
        @NonNull  Consumer<RemoteFile> remoteFileConsumer) throws IOException, InterruptedException  {
        Duration retry = Duration.ofSeconds(10);
        RemoteFile handle = null;
        try(final SSHClient sessionFactory = createClient();
            final SFTPClient sftp = sessionFactory.newSFTPClient()) {
            Instant start = Instant.now();
            long count = 0;
            RETRY:
            while (true) {
                count++;
                try {
                    log.debug("Checking for read {}", nepFile);
                    handle = sftp.open(nepFile, EnumSet.of(OpenMode.READ));
                    FileAttributes attributes = handle.fetchAttributes();
                    FileMetadata descriptor = FileMetadata.builder()
                        .size(handle.length())
                        .lastModified(Instant.ofEpochMilli(attributes.getMtime() * 1000))
                        .fileName(nepFile)
                        .build();
                    if (descriptorConsumer != null) {
                        try {
                            Proceed proceed = descriptorConsumer.apply(descriptor);
                            switch (proceed) {
                                case TRUE:
                                    break RETRY;
                                case RETRY:
                                    Slf4jHelper.debugOrInfo(log, count > 6, "{}: need retry. Waiting {}", nepFile, retry);
                                    Thread.sleep(retry.toMillis());
                                    continue RETRY;
                                case FALSE:
                                    return;

                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                } catch (SFTPException sftpe) {
                    if (timeout == null || timeout.equals(Duration.ZERO)) {
                        throw new IllegalStateException("File " + nepFile + " doesn't exist");
                    }
                    if (Duration.between(start, Instant.now()).compareTo(timeout) > 0) {
                        throw new IllegalStateException("File '" + nepFile + "' didn't appear in " + timeout);
                    }
                    Slf4jHelper.debugOrInfo(log, count > 6, "{}: {}. Waiting {} for retry", nepFile, sftpe.getMessage(), retry);
                    Thread.sleep(retry.toMillis());
                }
            }
        } finally {
            if (handle != null) {
                remoteFileConsumer.accept(handle);
                try {
                    handle.close();
                } catch(Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    protected SSHClient createClient() throws IOException {
        return SSHClientFactory
                .create(hostKey, ftpHost, username, password).get();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" +  username + "@" + ftpHost;
    }
}
