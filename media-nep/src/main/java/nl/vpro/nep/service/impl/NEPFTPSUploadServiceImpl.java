package nl.vpro.nep.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import jakarta.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ObservableInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
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
 *  This is a wrapper for ftp.nepworldwide.nl This is where we have to upload files for transcoding
 * <p>
 *  TODO: For the download service we had severe troubles with 'rekeying' (at the end worked around by calling command line scp). Would this not be an issue for upload?
 */
@Slf4j
@ManagedResource
public class NEPFTPSUploadServiceImpl implements NEPUploadService {

    @Getter
    private final String ftpHost;
    @Getter
    private final String username;
    private final String password;

    private Duration connectTimeout = Duration.ofSeconds(10L);
    private Duration socketTimeout = Duration.ofSeconds(10L);

    /**
     * This has something to do with the timeout for waiting until acknowledgements from the server?
     */
    private Duration sftpTimeout = Duration.ofSeconds(5);

    private int batchSize = 1024 * 1024 * 5;



    @Inject
    public NEPFTPSUploadServiceImpl(
        @Value("${nep.sourcing-service-upload.host}") String ftpHost,
        @Value("${nep.sourcing-service-upload.username}") String username,
        @Value("${nep.sourcing-service-upload.password}") String password,
        @Value("${nep.gatekeeper-upload.batchSize:52428800}") int batchSize

    ) {
        this.ftpHost = ftpHost;
        this.username = username;
        this.password = password;
        this.batchSize = batchSize;
        init();
    }

    protected NEPFTPSUploadServiceImpl(Properties properties) {
        this(
            properties.getProperty("nep.sourcing-service-upload.host"),
            properties.getProperty("nep.sourcing-service-upload.username"),
            properties.getProperty("nep.sourcing-service-upload.password"),
            NumberUtils.toInt(properties.getProperty("nep.gatekeeper-upload.batchSize"), 52428800)
        );
    }


    //@PostConstruct
    protected void init() {
        log.info("Started nep file transfer service for {}@{}", username, ftpHost);
    }


    private static final FileSizeFormatter FORMATTER = FileSizeFormatter.DEFAULT;


    /**
     * See MSE-5800. This below implementation will always result in file attributes to be set. Which as of 2024-06-04 suddenly seems to be a problem.
     * Using {@link #upload(SimpleLogger, String, Long, Path, boolean)} this is worked around (unless #{preserveAttributes} is set to true)
     */
    @Override
    public long upload(
        final @NonNull SimpleLogger logger,
        final @NonNull String nepFile,
        final @NonNull Long size,
        final @NonNull InputStream incomingStream,
        final boolean replaces) throws IOException {

        final Instant start = Instant.now();
        final long infoBatch = 1;

        FTPClient client = new FTPClient();
        try (ObservableInputStream observableInputStream = new ObservableInputStream(incomingStream)) {
            client.connect(ftpHost);
            client.setFileType(FTPSClient.BINARY_FILE_TYPE);
            //client.execAUTH("TLS");
            client.login(username, password);
            client.enterLocalPassiveMode();
            AtomicLong numberOfBytes = new AtomicLong(0L);

            client.deleteFile(nepFile);


            observableInputStream.add(new ObservableInputStream.Observer() {
                long nextLog = batchSize;
                @Override
                public void data(byte[] buffer, int offset, int length) throws IOException {
                    super.data(buffer, offset, length);
                    long b = numberOfBytes.addAndGet(length);
                    if (b > nextLog){
                        log();
                        nextLog = b + batchSize;
                    }
                }
                @Override
                public void data(int value) throws IOException {
                    numberOfBytes.incrementAndGet();
                }
                @Override
                public void closed() {
                    log();
                    Duration duration = Duration.between(start, Instant.now());
                    logger.info(
                        en("Ready uploading {}/{} (took {}, {})")
                            .nl("Klaar met uploaden van {}/{} (kostte: {}, {})")
                            .slf4jArgs(
                                FORMATTER.format(numberOfBytes),
                                FORMATTER.format(size),
                                duration,
                                FORMATTER.formatSpeed(numberOfBytes, duration))
                    );
                }
                private void log() {
                    Duration duration = Duration.between(start, Instant.now());
                    logger.info(
                        en("Uploaded {}/{} to {}:{} ({})")
                            .nl("Geüpload {}/{} naar {}:{} ({})")
                            .slf4jArgs(
                                FORMATTER.format(numberOfBytes.get()),
                                FORMATTER.format(size),
                                ftpHost, nepFile,
                                FORMATTER.formatSpeed(numberOfBytes, duration)
                            )
                    );
                }

            });


            client.storeFile(nepFile, observableInputStream);

            return numberOfBytes.get();
        } finally {
            IOUtils.closeQuietly(incomingStream);
             try {
                 client.logout();
                 client.disconnect();
             } catch (Exception e) {
                 log.warn("Error disconnecting from FTPS server: " + e.getMessage());
             }
        }
    }

    @Override
    public UploadResult upload(
        final @NonNull SimpleLogger logger,
        final @NonNull String nepFile,
        final @NonNull Long size,
        final @NonNull Path incomingFile,
        final boolean replaces) throws IOException {

        long bytes = upload(logger, nepFile, size, incomingFile.toUri().toURL().openStream(), replaces);


        return UploadResult.sizeOnly(bytes, ftpHost);
    }





    @Override
    public String getUploadString() {
        return username + "@" + ftpHost;
    }

    @Override
    public boolean isEnabled() {
        return StringUtils.isNotBlank(password);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + username + "@" + ftpHost;
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

    private Supplier<IllegalArgumentException> couldNotParse(String string) {
        return () -> new IllegalArgumentException("could not parse " + string);
    }


}


