package nl.vpro.nep.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.service.NEPUploadService;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.util.MultiLanguageString.en;


/**
 *  This is a wrapper for ftp.nepworldwide.nl This is were we have to upload file for transcoding
 *
 *  TODO: For the download service we had severe troubles with 'rekeying' (at the end worked around by calling command line scp). Would this not be an issue for upload?
 */
@Named("NEPUploadService")
@Slf4j
public class NEPSSHJUploadServiceImpl implements NEPUploadService {

    private final String sftpHost;
    private final String username;
    private final String password;
    private final String hostKey;

    @Getter
    @Setter
    private Duration connectTimeOut = Duration.ofSeconds(10L);

    @Getter
    @Setter
    private Duration socketTimeout = Duration.ofSeconds(10L);


    @Getter
    @Setter
    private Duration maxaliveClient = Duration.ofMinutes(3);


    ThreadLocal<SSHClientFactory.ClientHolder> sshClient = new ThreadLocal<>();

    Set<SSHClientFactory.ClientHolder> created = new HashSet<>();

    @Inject
    public NEPSSHJUploadServiceImpl(
        @Value("${nep.transcode.sftp.host}") String sftpHost,
        @Value("${nep.transcode.sftp.username}") String username,
        @Value("${nep.transcode.sftp.password}") String password,
        @Value("${nep.transcode.sftp.hostkey}") String hostKey
    ) {
        this.sftpHost = sftpHost;
        this.username = username;
        this.password = password;
        this.hostKey = hostKey;
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

    private final FileSizeFormatter formatter = FileSizeFormatter.DEFAULT;
    @Override
    public long upload(
        @Nonnull SimpleLogger logger,
        @Nonnull String nepFile,
        @Nonnull Long size,
        @Nonnull InputStream stream) throws IOException {
        Instant start = Instant.now();
        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);

        try(
            final SSHClient client = createClient().get();
            final SFTPClient sftp = client.newSFTPClient();

        ) {
            int split  = nepFile.lastIndexOf("/");
            if (split > 0) {
                sftp.mkdirs(nepFile.substring(0, split));
            }
            try (
                final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
                OutputStream out = handle.new RemoteFileOutputStream()
            ) {

                byte[] buffer = new byte[1014 * 1024];
                long infoBatch = buffer.length * 100;
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
                    if (numberOfBytes % infoBatch == 0) {
                        // updating spans in ngToast doesn't work...
                        //logger.info("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
                        logger.info(
                            en("Uploaded {}/{} to NEP")
                                .nl("Geüpload {}/{} naar NEP")
                                .slf4jArgs(formatter.format(numberOfBytes), formatter.format(size))
                                .build()
                        );
                    } else {
                        log.debug("Uploaded {}/{} bytes to NEP", formatter.format(numberOfBytes), formatter.format(size));
                    }
                }
                logger.info(
                    en("Ready uploading {}/{} bytes (took {})")
                        .nl("Klaar met uploaden van {}/{} bytes (kostte: {})")
                        .slf4jArgs(formatter.format(numberOfBytes), formatter.format(size), Duration.between(start, Instant.now()))
                        .build());
                return numberOfBytes;
            }
        }
    }



    @Override
    public String toString() {
        return username + "@" + sftpHost;
    }

    SSHClient getClient() throws IOException {
        SSHClientFactory.ClientHolder client = sshClient.get();
        if (client == null || ! client.get().isConnected() || Duration.between(client.getCreationTime(), Instant.now()).compareTo(maxaliveClient) > 0) {
            if (client != null) {
                if (client.get().isConnected()) {
                    client.get().disconnect();
                }
                created.remove(client);
            }
            client = createClient();
            created.add(client);
            sshClient.set(client);
        }
        return client.get();
    }

    protected synchronized SSHClientFactory.ClientHolder createClient() throws IOException {

        SSHClientFactory.ClientHolder client = SSHClientFactory.create(hostKey, sftpHost, username, password);

        client.get().setTimeout((int) socketTimeout.toMillis());
        client.get().setConnectTimeout((int) connectTimeOut.toMillis());


        log.info("Created client {} with connection {}", client, client.get().getConnection().getTransport());

        return client;

    }



}
