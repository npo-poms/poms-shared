package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.CommandExecutor;
import nl.vpro.util.CommandExecutorImpl;
import nl.vpro.util.FileMetadata;

/**
 * See MSE-4032. It's kind of a disgrace that we have to fall back to external commands...
 *
 * I first tried curl, to no avail either.
 *
 * Older scp clients would give troubles too. In the end Dick realized that, and pointed to a more up to date client on the poms server.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Named("NEPDownloadService")
@Slf4j
public class NEPScpDownloadServiceImpl implements NEPDownloadService {


    private final String url;
    private final CommandExecutor scp;
    private final NEPSSHJDownloadServiceImpl sshj;
    private final static Map<String, File> knownHosts = new HashMap<>();


    public NEPScpDownloadServiceImpl(
        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostkey,
        @Value("${executables.scp}") List<String> scpExecutables,
        @Value("${executables.sshpass}") List<String> sshpassExecutables
    ) {
        this.url = username + "@" + ftpHost;

        File scpcommand = CommandExecutorImpl
            .getExecutableFromStrings(scpExecutables)
            .orElseThrow(IllegalArgumentException::new);
        // just used for the checkAvailability call (actually for the descriptorConsumer callback)
        sshj = new NEPSSHJDownloadServiceImpl(ftpHost, username, password, hostkey);
        CommandExecutor scptry = null;
        try {
            File tempFile = knownHosts.computeIfAbsent(hostkey, (k) -> knowHosts(ftpHost, hostkey));
            if (! tempFile.exists()) {
                knownHosts.remove(hostkey);
                tempFile = knownHosts.computeIfAbsent(hostkey, (k) -> knowHosts(ftpHost, hostkey));
            }
            scptry = CommandExecutorImpl.builder()
                .executablesPaths(sshpassExecutables)
                .wrapLogInfo((message) -> message.toString().replaceAll(password, "??????"))
                .useFileCache(true)
                .commonArgs(Arrays.asList("-p", password, scpcommand.getAbsolutePath(), "-q", "-o", "StrictHostKeyChecking=yes", "-o", "UserKnownHostsFile=" + tempFile))
                .build();


        } catch (RuntimeException rte) {
            log.error(rte.getMessage(), rte);
        }
        scp = scptry;
    }

    protected File knowHosts(String ftpHost, String hostkey) {
        try {
            File f = File.createTempFile("known_hosts", "tmp");
            try (PrintWriter writer = new PrintWriter(f)) {
                writer.println(ftpHost + " ssh-rsa " + hostkey);
            }
            f.deleteOnExit();
            return f;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void download(
        @Nonnull String nepFile,
        @Nonnull Supplier<OutputStream> outputStream,
        @Nonnull Duration timeout,
        Function<FileMetadata, Boolean> descriptorConsumer) {
        try {
            checkAvailability(nepFile, timeout, descriptorConsumer);
            try (OutputStream out = outputStream.get()){
                if (out != null) {
                    scp.execute(out, getUrl(nepFile), "/dev/stdout");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (CommandExecutor.BrokenPipe bp) {
            log.debug(bp.getMessage());
            throw bp;
        } catch (RuntimeException rte) {
            log.error(rte.getMessage(), rte);
            throw rte;
        }

    }

    protected String getUrl(String nepFile) {
        return url + ":" + nepFile;
    }


    protected void checkAvailability(
        @Nonnull String nepFile,
        @Nullable Duration timeout,
        @Nonnull Function<FileMetadata, Boolean> descriptorConsumer) throws IOException {
        sshj.checkAvailabilityAndConsume(nepFile, timeout, descriptorConsumer, (handle) -> {});
    }

    @Override
    public String toString () {
        return scp + " " + url ;
    }

}
