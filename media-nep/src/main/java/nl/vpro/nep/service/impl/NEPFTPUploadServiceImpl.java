package nl.vpro.nep.service.impl;

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

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.vpro.logging.SimpleLogger;
import nl.vpro.nep.service.NEPFTPUploadService;
import nl.vpro.util.FileSizeFormatter;

@Service("NEPFTPUploadService")
@Slf4j
public class NEPFTPUploadServiceImpl implements NEPFTPUploadService {

    private final String sftpHost;
    private final String username;
    private final String password;
    private final String hostKey;

    private final Duration connectTimeOut = Duration.ofSeconds(10L);
    private final Duration socketTimeout = Duration.ofSeconds(10L);

    private SSHClient sshClient;

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

        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);
    }
    @PreDestroy
    public void destroy() throws IOException {
        if (sshClient != null) {
            sshClient.close();
        }
    }
    private final FileSizeFormatter formatter = FileSizeFormatter.builder().build();
    @Override
    public long upload(SimpleLogger logger, String nepFile, Long size, InputStream stream) throws IOException {
        Instant start = Instant.now();
        log.info("Started nep file transfer service for {} @ {} (hostkey: {})", username, sftpHost, hostKey);
        if (sshClient == null || ! sshClient.isConnected()) {
            sshClient = SSHClientFactory.create(hostKey, sftpHost, username, password);
            sshClient.setTimeout((int) socketTimeout.toMillis());
            sshClient.setConnectTimeout((int) connectTimeOut.toMillis());
        }

        final SFTPClient sftp = sshClient.newSFTPClient();
        final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.CREAT, OpenMode.WRITE));

        OutputStream out = handle.new RemoteFileOutputStream();
        byte[] buffer= new byte[1014 * 1024];
        long infoBatch = buffer.length * 10;
        long numberofBytes = 0;
        int n;
        while (IOUtils.EOF != (n = stream.read(buffer))) {
            out.write(buffer, 0, n);
            numberofBytes += n;
            if (numberofBytes % infoBatch == 0) {
                logger.info("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Uploaded {}/{} bytes to NEP", formatter.format(numberofBytes), formatter.format(size));
                }
            }
        }
        logger.info("Ready uploading {}/{} bytes (took {})", formatter.format(numberofBytes),  formatter.format(size), Duration.between(start, Instant.now()));
        handle.close();
        sftp.close();
        return numberofBytes;
    }


    @Override
    public String toString() {
        return username + "@" + sftpHost;
    }
}
