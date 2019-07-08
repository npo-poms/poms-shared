package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.LoggerOutputStream;
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



    @Inject
    public NEPScpDownloadServiceImpl(
        @Value("${nep.itemizer-download.host}") String ftpHost,
        @Value("${nep.itemizer-download.username}") String username,
        @Value("${nep.itemizer-download.password}") String password,
        @Value("${nep.itemizer-download.hostkey}") String hostkey,
        @Value("${nep.itemizer-download.scp.useFileCache}") boolean useFileCache,
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
                .useFileCache(useFileCache)
                .commonArgs(Arrays.asList("-p", password, scpcommand.getAbsolutePath(), "-o", "StrictHostKeyChecking=yes", "-o", "UserKnownHostsFile=" + tempFile))
                .build();


        } catch (RuntimeException rte) {
            log.error(rte.getMessage(), rte);
        }
        scp = scptry;
    }

    protected NEPScpDownloadServiceImpl(Properties properties) {
        this(
            properties.getProperty("nep.itemizer-download.host"),
            properties.getProperty("nep.itemizer-download.username"),
            properties.getProperty("nep.itemizer-download.password"),
            properties.getProperty("nep.itemizer-download.hostkey"),
            true,
            Arrays.asList("/local/bin/scp", "/usr/bin/scp"),
            Arrays.asList("/usr/bin/sshpass", "/opt/local/bin/sshpass")
        );
    }

    protected File knowHosts(String ftpHost, String hostkey) {
        try {
            File f = File.createTempFile("known_hosts", ".tmp");
            try (PrintWriter writer = new PrintWriter(f)) {
                writer.println(ftpHost + " ssh-rsa " + hostkey);
            }
            f.deleteOnExit();
            log.info("Created {}", f);
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
        Function<FileMetadata, Proceed> descriptorConsumer) {
        int exitCode = 0;
        String url = getUrl(nepFile);
        try {
            checkAvailability(nepFile, timeout, descriptorConsumer);
            try (OutputStream out = outputStream.get()){
                if (out != null) {
                    exitCode = scp.execute(out, LoggerOutputStream.error(log), url, "/dev/stdout");
                } else {
                    log.warn("Can't download from null stream to " + url);
                }
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        } catch (CommandExecutor.BrokenPipe bp) {
            log.debug(bp.getMessage());
            throw bp;
        } catch (RuntimeException rte) {
            log.error(rte.getMessage(), rte);
            throw rte;
        }
        if (exitCode != 0) {
            throw new CommandExecutor.ExitCodeException("SCP command  from " + url + " failed", exitCode);
        }

    }

    protected String getUrl(String nepFile) {
        return url + ":" + nepFile;
    }


    protected void checkAvailability(
        @Nonnull String nepFile,
        @Nullable Duration timeout,
        @Nonnull Function<FileMetadata, Proceed> descriptorConsumer) throws IOException, InterruptedException {
        sshj.checkAvailabilityAndConsume(nepFile, timeout, descriptorConsumer, (handle) -> {});
    }

    @Override
    public String toString () {
        return scp + " " + url ;
    }

}
