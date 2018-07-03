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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.service.NEPUploadService;
import nl.vpro.util.FileSizeFormatter;


/**
 *  This is a wrapper for ftp.nepworldwide.nl This is were we have to upload file for transcoding
 */
@Named("NEPUploadService")
@Slf4j
public class NEPFTPUploadServiceImpl implements NEPUploadService {

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

    ThreadLocal<SSHClient> sshClient = new ThreadLocal<>();

    Set<SSHClient> created = new HashSet<>();

    @Inject
    public NEPFTPUploadServiceImpl(
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
        for (SSHClient client : created) {
            log.info("Closing {}", client);
            client.close();
        }
    }

    private final FileSizeFormatter formatter = FileSizeFormatter.DEFAULT;
    @Override
    public long upload(SimpleLogger logger, String nepFile, Long size, InputStream stream) throws IOException {
        Instant start = Instant.now();
        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);
        createClient();
        try(
            final SFTPClient sftp = getClient().newSFTPClient();
            final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));
            OutputStream out = handle.new RemoteFileOutputStream();
        ) {
            byte[] buffer = new byte[1014 * 1024];
            long infoBatch = buffer.length * 100;
            long numberofBytes = 0;
            int n;
            long timesZero = 0;
            while (IOUtils.EOF != (n = stream.read(buffer))) {
                out.write(buffer, 0, n);
                numberofBytes += n;
                if (n == 0) {
                    timesZero++;
                } else {
                    timesZero = 0;
                }
                if (numberofBytes == size && timesZero > 5) {
                    log.info("Number of bytes reached, breaking (though we didn't see EOF yet)");
                    break;
                }
                if (numberofBytes % infoBatch == 0) {
                    // updating spans in ngToast doesn't work...
                    //logger.info("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
                    logger.info("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
                    }
                }
            }
            logger.info("Ready uploading {}/{} bytes (took {})", formatter.format(numberofBytes), formatter.format(size), Duration.between(start, Instant.now()));
            return numberofBytes;
        }
    }


    @Override
    public String toString() {
        return username + "@" + sftpHost;
    }

    SSHClient getClient() throws IOException {
        SSHClient client = sshClient.get();
        if (client == null || ! client.isConnected()) {
            if (client != null) {
                created.remove(client);
            }
            client = createClient();
            sshClient.set(client);
        }
        return client;
    }

    protected synchronized  SSHClient createClient() throws IOException {
        SSHClient client = SSHClientFactory.create(hostKey, sftpHost, username, password);
        client.setTimeout((int) socketTimeout.toMillis());
        client.setConnectTimeout((int) connectTimeOut.toMillis());
        log.info("Created client {}", client);
        created.add(client);
        return client;

    }

}
