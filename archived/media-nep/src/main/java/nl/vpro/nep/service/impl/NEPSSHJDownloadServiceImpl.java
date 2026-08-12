package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.*;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.nep.service.exception.NEPFileNotAppearedTimelyException;
import nl.vpro.nep.service.exception.NEPFileNotFoundException;
import nl.vpro.util.FileMetadata;


/**
 * This is a wrapper for sftp-itemizer.nepworldwide.nl This is where itemize results are placed by NEP
 * <p>
 * Not used, because of <a href="https://github.com/hierynomus/sshj/issues/432">this issue</a>
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

    protected NEPSSHJDownloadServiceImpl(Properties properties) {
        this(
            properties.getProperty("nep.itemizer-download.host"),
            properties.getProperty("nep.itemizer-download.username"),
            properties.getProperty("nep.itemizer-download.password"),
            properties.getProperty("nep.itemizer-download.hostkey")
        );
    }


    @PostConstruct
    public void init() {
        log.info("NEP download service for {}@{}", username, ftpHost);
    }

    @Override
    public void download(
        @NonNull String directory,
        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout, Function<FileMetadata, Proceed> descriptorConsumer) {
        log.info("Started nep file transfer service for {}@{} (hostkey: {})", username, ftpHost, hostKey);
        if (StringUtils.isBlank(nepFile)) {
            throw new IllegalArgumentException();
        }
        try {
            checkAvailabilityAndConsume(directory, nepFile, timeout, descriptorConsumer, (handle) -> {
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
                            log.debug(ioe.getMessage());
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

    @SuppressWarnings("BusyWait")
    protected void checkAvailabilityAndConsume   (
        @NonNull String directory,
        @NonNull String f,
        @Nullable Duration timeout,
        @Nullable Function<FileMetadata, Proceed> descriptorConsumer,
        @NonNull  Consumer<RemoteFile> remoteFileConsumer) throws IOException, InterruptedException  {
        final Duration retry = Duration.ofSeconds(10);
        final String nepFile = NEPDownloadService.join(directory, f);
        try(final SSHClient sessionFactory = createClient();
            final SFTPClient sftp = sessionFactory.newSFTPClient()) {
            final Instant start = Instant.now();
            long count = 0;
            RemoteFile handle = null;
            try {
                RETRY:
                while (true) {
                    count++;
                    try {
                        log.debug("Checking for read {}", nepFile);
                        handle = sftp.open(nepFile, EnumSet.of(OpenMode.READ));
                        final FileAttributes attributes = handle.fetchAttributes();
                        final FileMetadata descriptor = FileMetadata.builder()
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
                            throw new NEPFileNotFoundException(sftpe, "File " + nepFile + " doesn't exist");
                        }
                        if (Duration.between(start, Instant.now()).compareTo(timeout) > 0) {
                            throw new NEPFileNotAppearedTimelyException(sftpe, "File '" + nepFile + "' didn't appear in " + timeout);
                        }
                        Slf4jHelper.debugOrInfo(log, count > 6, "{}: {}. Waiting {} for retry", nepFile, sftpe.getMessage(), retry);
                        Thread.sleep(retry.toMillis());
                        if (handle != null) {
                            try {
                                handle.close();
                            } finally {
                                handle = null;
                            }
                        }
                    }
                }
            } finally {
                if (handle != null) {
                    try {
                        remoteFileConsumer.accept(handle);
                    } finally {
                        try {
                            handle.close();
                        } catch (Exception e) {
                            log.warn("During closing of {}: {}", handle, e.getMessage());

                        }
                    }
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
        return getClass().getSimpleName() + ":" +  getDownloadString();
    }

    @Override
    public String getDownloadString() {
        return "ssj:" + username + "@" + ftpHost;

    }
}
