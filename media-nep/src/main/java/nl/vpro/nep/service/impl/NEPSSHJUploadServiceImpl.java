package nl.vpro.nep.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Supplier;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

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

    @Getter
    @Setter
    private int  batchSize = 1024 * 1024 * 5;


    @Inject
    public NEPSSHJUploadServiceImpl(
        @Value("${nep.gatekeeper-upload.host}") String sftpHost,
        @Value("${nep.gatekeeper-upload.username}") String username,
        @Value("${nep.gatekeeper-upload.password}") String password,
        @Value("${nep.gatekeeper-upload.hostkey}") String hostKey,
        @Value("${nep.gatekeeper-upload.batchSize:5242880}") int batchSize

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
            NumberUtils.toInt(properties.getProperty("nep.gatekeeper-upload.batchSize"), 5242880)
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
        final long infoBatch = 10;


        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);
        logger.info(
            en("Uploading to {}:{}")
                .nl("Uploaden naar {}:{}")
                .slf4jArgs(sftpHost, nepFile)
        );
        return IOUtils.copy(incomingStream, OutputStream.nullOutputStream());
    }

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

    private Supplier<IllegalArgumentException> couldNotParse(String string){
        return () -> new IllegalArgumentException("could not parse " + string);
    }

    synchronized SSHClientFactory.ClientHolder createClient() throws IOException {

        SSHClientFactory.ClientHolder client = SSHClientFactory.create(
            hostKey,
            sftpHost,
            username,
            password
        );

        client.get().setTimeout((int) socketTimeout.toMillis());
        client.get().setConnectTimeout((int) connectTimeout.toMillis());
        client.get().useCompression();


        log.info("Created client {} with connection {}", client, client.get().getConnection().getTransport());
        return client;

    }



}
