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
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.Slf4jHelper;
import nl.vpro.nep.service.FileDescriptor;
import nl.vpro.nep.service.NEPDownloadService;

import static org.apache.commons.io.IOUtils.copy;


/**
 * This is a wrapper for sftp-itemizer.nepworldwide.nl This is were itemize results are placed by NEP
 */
//@Named("NEPDownloadService") // It doesn't *** work
@Slf4j
public class NEPFTPDownloadServiceImpl implements NEPDownloadService {


    private final String ftpHost;
    private final String username;
    private final String password;
    private final String hostKey;

    @Inject
    public NEPFTPDownloadServiceImpl(

        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostKey
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
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        log.info("Started nep file transfer service for {}@{} (hostkey: {})", username, ftpHost, hostKey);
        if (StringUtils.isBlank(nepFile)) {
            throw new IllegalArgumentException();
        }
        try (
            final SSHClient sessionFactory = createClient();
            final SFTPClient sftp = sessionFactory.newSFTPClient()) {
            Instant start = Instant.now();
            InputStream in;
            long count = 0;
            FileDescriptor descriptor;
            while (true) {
                count++;
                try {
                    final RemoteFile handle = sftp.open(nepFile, EnumSet.of(OpenMode.READ));
                    in = handle.new ReadAheadRemoteFileInputStream(16);
                    FileAttributes attributes = handle.fetchAttributes();
                    descriptor = FileDescriptor.builder()
                        .size(handle.length())
                        .lastModified(Instant.ofEpochMilli(attributes.getMtime()))
                        .fileName(nepFile)
                        .build();
                    if (descriptorConsumer != null) {
                        try {
                            boolean proceeed = descriptorConsumer.apply(descriptor);
                            if (! proceeed) {
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
                        throw new IllegalStateException("File " + nepFile + " didn't appear in " + timeout);
                    }
                    Slf4jHelper.log(log, count < 6 ? Level.DEBUG :  Level.INFO,"{}: {}. Waiting for retry", nepFile, sftpe.getMessage());
                    Thread.sleep(Duration.ofSeconds(10).toMillis());
                }
            }
            log.info("File {} ({} bytes) appeared in {}, now copying to {}.", nepFile,  descriptor == null ? "?" : descriptor.getSize(), Duration.between(start, Instant.now()), outputStream);

            long copy = copy(in, outputStream, 1024 * 10);
            in.close();

            log.info("Copied {} bytes", copy);
        } catch (SFTPException sfte) {
            log.error(sfte.getMessage());
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected SSHClient createClient() throws IOException {
        return SSHClientFactory
                .create(hostKey, ftpHost, username, password);
    }


    @Override
    public String toString() {
        return username + "@" + ftpHost;
    }
}
