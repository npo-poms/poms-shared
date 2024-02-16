package nl.vpro.nep.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.service.NEPUploadService;
import nl.vpro.util.FileSizeFormatter;
import nl.vpro.util.TimeUtils;

import static nl.vpro.i18n.MultiLanguageString.en;


/**
 *  This is a wrapper for ftp.nepworldwide.nl This is were we have to upload file for transcoding
 * <p>
 *  TODO: For the download service we had severe troubles with 'rekeying' (at the end worked around by calling command line scp). Would this not be an issue for upload?
 */
@Slf4j
@ManagedResource
public class NEPSSHJUploadServiceImpl implements NEPUploadService {

    @Getter
    private final String sftpHost;
    @Getter
    private final String username;
    private final String password;
    @Getter
    private final String hostKey;

    private Duration connectTimeout = Duration.ofSeconds(10L);
    private Duration socketTimeout = Duration.ofSeconds(10L);

    /**
     * This has something to do with the timeout for waiting until acknowledgements from the server?
     */
    private Duration sftpTimeout = Duration.ofSeconds(5);

    private int  batchSize = 1024 * 1024 * 5;


    @Inject
    public NEPSSHJUploadServiceImpl(
        @Value("${nep.gatekeeper-upload.host}") String sftpHost,
        @Value("${nep.gatekeeper-upload.username}") String username,
        @Value("${nep.gatekeeper-upload.password}") String password,
        @Value("${nep.gatekeeper-upload.hostkey}") String hostKey,
        @Value("${nep.gatekeeper-upload.batchSize:52428800}") int batchSize

    ) {
        this.sftpHost = sftpHost;
        this.username = username;
        this.password = password;
        this.hostKey = hostKey;
        this.batchSize = batchSize;
        init();
    }

    protected NEPSSHJUploadServiceImpl(Properties properties) {
        this(
            properties.getProperty("nep.gatekeeper-upload.host"),
            properties.getProperty("nep.gatekeeper-upload.username"),
            properties.getProperty("nep.gatekeeper-upload.password"),
            properties.getProperty("nep.gatekeeper-upload.hostkey"),
            NumberUtils.toInt(properties.getProperty("nep.gatekeeper-upload.batchSize"), 52428800)
        );
    }


    //@PostConstruct
    protected void init() {
        log.info("Started nep file transfer service for {}@{} (hostkey: {})", username, sftpHost, hostKey);
    }

    @PreDestroy
    public void destroy() {

    }

    private static final FileSizeFormatter FORMATTER = FileSizeFormatter.DEFAULT;

    @Override
    public long upload(
        final @NonNull SimpleLogger logger,
        final @NonNull String nepFile,
        final @NonNull Long size,
        final @NonNull InputStream incomingStream,
        final boolean replaces) throws IOException {

        final Instant start = Instant.now();
        final long infoBatch = 1;


        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);
        logger.info(
            en("Uploading to {}:{}")
                .nl("Uploaden naar {}:{}")
                .slf4jArgs(sftpHost, nepFile)
        );
        try(
            final SSHClientFactory.ClientHolder client = createClient();
            final SFTPClient sftp = client.get().newSFTPClient()
        ) {
            sftp.getSFTPEngine().setTimeoutMs((int) sftpTimeout.toMillis());
            final int split  = nepFile.lastIndexOf('/');
            if (split > 0) {
                sftp.mkdirs(nepFile.substring(0, split));
            }
            if (! replaces) {
                if (!checkExistence(logger, sftp, nepFile, size)) {
                    log.info("File {} already exists, not replacing", nepFile);
                    return -1;
                }
            }
            long numberOfBytes = 0;
            try (
                final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
                final OutputStream out = handle.new RemoteFileOutputStream()
            ) {
                final byte[] buffer = new byte[batchSize];
                long prevBatchCount = -1;
                long batchCount = 0;
                int n;
                while (IOUtils.EOF != (n = incomingStream.read(buffer))) {
                    if (n == 0) {
                        log.debug("InputStream#read(buffer) gave zero bytes.");
                        continue;
                    }
                    out.write(buffer, 0, n);
                    numberOfBytes += n;

                    batchCount = numberOfBytes / (batchSize * infoBatch);
                    if (prevBatchCount != batchCount) {
                        prevBatchCount = batchCount;
                        final Duration duration = Duration.between(start, Instant.now());

                        // updating spans in ngToast doesn't work...
                        logger.info(
                            en("Uploaded {}/{} to {}:{} ({})")
                                .nl("Geüpload {}/{} naar {}:{} ({})")
                                .slf4jArgs(
                                    FORMATTER.format(numberOfBytes),
                                    FORMATTER.format(size),
                                    sftpHost, nepFile,
                                    FORMATTER.formatSpeed(numberOfBytes, duration)
                                )
                        );
                    } else {
                        log.debug("Uploaded {}/{} bytes to NEP", FORMATTER.format(numberOfBytes), FORMATTER.format(size));
                    }
                }
                final Duration duration = Duration.between(start, Instant.now());
                logger.info(
                    en("Ready uploading {}/{} (took {}, {})")
                        .nl("Klaar met uploaden van {}/{} (kostte: {}, {})")
                        .slf4jArgs(
                            FORMATTER.format(numberOfBytes),
                            FORMATTER.format(size),
                            duration,
                            FORMATTER.formatSpeed(numberOfBytes, duration))
                );
                assert  handle.length() == numberOfBytes;
                return numberOfBytes;
            } catch (SFTPException sftpException) {
                Throwable e = sftpException;
                if (sftpException.getCause() != null) {
                    e = sftpException.getCause();
                }
                logger.warn("error from sftp: {}", e.getMessage(), e);
                if (e instanceof TimeoutException) {
                    if (numberOfBytes == size) {
                        log.info("But the number of transferred bytes is correct. So we assume it is ok");
                        return numberOfBytes;
                    }
                }
                throw sftpException;
            }
        }
    }

    /**
     * Checks whether the file is already existing on the remote end, and has the expected size.
     * @return false if the file is already there and has the expected size, true if it is not there or has a different size.
     */
    private boolean checkExistence(
        SimpleLogger logger,
        SFTPClient sftp,
        String nepFile,
        long size
    ) {
        try (
            final RemoteFile handleToCheck = sftp.open(nepFile, EnumSet.of(OpenMode.READ))
        ) {
            FileAttributes attributes = handleToCheck.fetchAttributes();
            if (attributes.getSize() != size) {
                logger.warn("Found existing, but size is not equal {} {} != {}",
                    nepFile, attributes.getSize(), size);
            } else {
                logger.info("Found existing {}", attributes);
            }
            return false;
        } catch (SFTPException sftpException) {
            log.warn("For {}: {}", nepFile, sftpException.getMessage());
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public String getUploadString() {
        return username + "@" + sftpHost;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + username + "@" + sftpHost;
    }

    @ManagedAttribute
    public String getConnectTimeout() {
        return String.valueOf(connectTimeout);
    }

    @ManagedAttribute
    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = TimeUtils.parseDuration(connectTimeout).orElseThrow(couldNotParse(connectTimeout));
    }

    @ManagedAttribute
    public String getSocketTimeout() {
        return String.valueOf(socketTimeout);
    }

    @ManagedAttribute
    public void setSocketTimeout(String socketTimeout) {
        this.socketTimeout = TimeUtils.parseDuration(socketTimeout).orElseThrow(couldNotParse(socketTimeout));
    }

    @ManagedAttribute
    public String getSftpTimeout() {
        return String.valueOf(sftpTimeout);
    }

    @ManagedAttribute
    public void setSftpTimeout(String sftpTimeout) {
        this.sftpTimeout = TimeUtils.parseDuration(sftpTimeout).orElseThrow(couldNotParse(sftpTimeout));
    }

    @ManagedAttribute
    public int getBatchSize() {
        return batchSize;
    }

    @ManagedAttribute
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    private Supplier<IllegalArgumentException> couldNotParse(String string){
        return () -> new IllegalArgumentException("could not parse " + string);
    }

    protected synchronized SSHClientFactory.ClientHolder createClient() throws IOException {

        SSHClientFactory.ClientHolder client = SSHClientFactory.create(
            hostKey,
            sftpHost,
            username,
            password
        );

        client.get().setTimeout((int) socketTimeout.toMillis());
        client.get().setConnectTimeout((int) connectTimeout.toMillis());

        log.info("Created client {} with connection {}", client, client.get().getConnection().getTransport());
        return client;

    }



}
