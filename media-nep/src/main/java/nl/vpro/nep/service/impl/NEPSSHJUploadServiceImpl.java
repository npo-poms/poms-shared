package nl.vpro.nep.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.service.NEPUploadService;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.i18n.MultiLanguageString.en;


/**
 *  This is a wrapper for ftp.nepworldwide.nl This is were we have to upload file for transcoding
 *
 *  TODO: For the download service we had severe troubles with 'rekeying' (at the end worked around by calling command line scp). Would this not be an issue for upload?
 */
@Slf4j
public class NEPSSHJUploadServiceImpl implements NEPUploadService {

    @Getter
    private final String sftpHost;
    @Getter
    private final String username;
    private final String password;
    @Getter
    private final String hostKey;

    @Getter
    @Setter
    private Duration connectTimeOut = Duration.ofSeconds(10L);

    @Getter
    @Setter
    private Duration socketTimeout = Duration.ofSeconds(10L);

    /**
     * This has something to do with the timeout for waiting until acknowledgements from the server?
     */
    @Getter
    @Setter
    private Duration sftpTimeout = Duration.ofSeconds(5);

    Set<SSHClientFactory.ClientHolder> created = new HashSet<>();

    @Inject
    public NEPSSHJUploadServiceImpl(
        @Value("${nep.gatekeeper-upload.host}") String sftpHost,
        @Value("${nep.gatekeeper-upload.username}") String username,
        @Value("${nep.gatekeeper-upload.password}") String password,
        @Value("${nep.gatekeeper-upload.hostkey}") String hostKey
    ) {
        this.sftpHost = sftpHost;
        this.username = username;
        this.password = password;
        this.hostKey = hostKey;
    }

    protected NEPSSHJUploadServiceImpl(Properties properties) {
        this(
            properties.getProperty("nep.gatekeeper-upload.host"),
            properties.getProperty("nep.gatekeeper-upload.username"),
            properties.getProperty("nep.gatekeeper-upload.password"),
            properties.getProperty("nep.gatekeeper-upload.hostkey")
        );
    }


    @PostConstruct
    public void init() {
        log.info("Started nep file transfer service for {}@{} (hostkey: {})", username, sftpHost, hostKey);
    }

    @PreDestroy
    public void destroy() throws IOException {
        for (SSHClientFactory.ClientHolder client : created) {
            log.info("Closing {}", client);
            client.get().disconnect();
        }
    }

    private final static FileSizeFormatter FORMATTER = FileSizeFormatter.DEFAULT;

    @Override
    public long upload(
        @NonNull SimpleLogger logger,
        @NonNull String nepFile,
        @NonNull Long size,
        @NonNull InputStream stream,
        boolean replaces) throws IOException {
        Instant start = Instant.now();
        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);
        logger.info(
            en("Uploading to {}:{}")
                .nl("Uploaden naar {}:{}")
            .slf4jArgs(sftpHost, nepFile)
            .build());
        try(
            final SSHClient client = createClient().get();
            final SFTPClient sftp = client.newSFTPClient()
        ) {
            sftp.getSFTPEngine().setTimeoutMs((int) sftpTimeout.toMillis());
            int split  = nepFile.lastIndexOf("/");
            if (split > 0) {
                sftp.mkdirs(nepFile.substring(0, split));
            }
            if (! replaces) {
                try (
                    final RemoteFile handleToCheck = sftp.open(nepFile, EnumSet.of(OpenMode.READ))
                ) {
                    FileAttributes attributes = handleToCheck.fetchAttributes();
                    if (attributes.getSize() != size) {
                        logger.warn("Found existing, but size is not equal {} {} != {}", nepFile, attributes.getSize(), size);
                    } else {
                        logger.info("Found existing {}", attributes);
                    }
                    return -1L;
                } catch (SFTPException sftpException) {
                    log.warn("For {}: {}", nepFile, sftpException.getMessage());
                }
            }
            try (
                final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
                OutputStream out = handle.new RemoteFileOutputStream()
            ) {

                byte[] buffer = new byte[1014 * 1024];
                long infoBatch = 10;
                long batchCount = 0;
                long numberOfBytes = 0;
                int n;
                long timesZero = 0;
                while (IOUtils.EOF != (n = stream.read(buffer))) {
                    out.write(buffer, 0, n);
                    numberOfBytes += n;
                    if (n == 0) {
                        timesZero++;
                    } else {
                        timesZero = 0;
                    }
                    if (numberOfBytes == size && timesZero > 5) {
                        log.info("Number of bytes reached, breaking (though we didn't see EOF yet)");
                        break;
                    }
                    if (batchCount++ % infoBatch == 0) {
                        // updating spans in ngToast doesn't work...
                        //logger.info("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
                        logger.info(
                            en("Uploaded {}/{} to {}:{}")
                                .nl("Geüpload {}/{} naar {}:{}")
                                .slf4jArgs(FORMATTER.format(numberOfBytes), FORMATTER.format(size), sftpHost, nepFile)
                                .build());
                    } else {
                        log.debug("Uploaded {}/{} bytes to NEP", FORMATTER.format(numberOfBytes), FORMATTER.format(size));
                    }
                }
                Duration duration = Duration.between(start, Instant.now());
                logger.info(
                    en("Ready uploading {}/{} (took {}, {})")
                        .nl("Klaar met uploaden van {}/{} (kostte: {}, {})")
                        .slf4jArgs(
                            FORMATTER.format(numberOfBytes),
                            FORMATTER.format(size),
                            duration,
                            FORMATTER.formatSpeed(numberOfBytes, duration))
                        .build());
                return numberOfBytes;
            } catch (SFTPException sftpException) {
                log.info("cause {}", sftpException.getCause().getMessage(), sftpException.getCause());
                throw sftpException;
            }
        }
    }

    @Override
    public String getUploadString() {
        return username + "@" + sftpHost;

    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + username + "@" + sftpHost;
    }

    protected synchronized SSHClientFactory.ClientHolder createClient() throws IOException {

        SSHClientFactory.ClientHolder client = SSHClientFactory.create(
            hostKey,
            sftpHost,
            username,
            password
        );

        client.get().setTimeout((int) socketTimeout.toMillis());
        client.get().setConnectTimeout((int) connectTimeOut.toMillis());

        log.info("Created client {} with connection {}", client, client.get().getConnection().getTransport());

        return client;

    }



}
