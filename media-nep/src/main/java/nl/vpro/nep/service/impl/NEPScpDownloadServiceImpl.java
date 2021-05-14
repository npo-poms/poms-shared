package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.nep.service.exception.NEPException;
import nl.vpro.util.*;

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
    private final Duration waitBetweenRetries = Duration.ofSeconds(10);
    private final int maxDownloadRetries;


    @Inject
    public NEPScpDownloadServiceImpl(
        @Value("${nep.itemizer-download.host}") String ftpHost,
        @Value("${nep.itemizer-download.username}") String username,
        @Value("${nep.itemizer-download.password}") String password,
        @Value("${nep.itemizer-download.hostkey}") String hostkey,
        @Value("${nep.itemizer-download.scp.useFileCache}") boolean useFileCache,
        @Value("${executables.scp}") List<String> scpExecutables,
        @Value("${executables.sshpass}") List<String> sshpassExecutables,
        @Value("${nep.itemizer-download.maxDownloadRetries}") int maxDownloadRetries,
        @Value("${nep.itemizer-download.debugSsh}") boolean debugSsh
    ) {
        this.url = username + "@" + ftpHost;
        this.maxDownloadRetries = maxDownloadRetries;
        File scpcommand = CommandExecutorImpl
            .getExecutableFromStrings(scpExecutables)
            .orElseThrow(IllegalArgumentException::new);
        // just used for the checkAvailability call (actually for the descriptorConsumer callback)
        sshj = new NEPSSHJDownloadServiceImpl(ftpHost, username, password, hostkey);
        CommandExecutor scptry = null;
        try {
            File tempFile = knownHosts.computeIfAbsent(hostkey, (k) -> knowHosts(ftpHost, hostkey));
            if (!tempFile.exists()) {
                knownHosts.remove(hostkey);
                tempFile = knownHosts.computeIfAbsent(hostkey, (k) -> knowHosts(ftpHost, hostkey));
            }

            CommandExecutorImpl.Builder builder = CommandExecutorImpl.builder()
                .executablesPaths(sshpassExecutables)
                .wrapLogInfo((message) -> message.toString().replaceAll(password, "??????"))
                .useFileCache(useFileCache)
                .logger(log)

                .commonArg(
                    "-p", password,
                    scpcommand.getAbsolutePath(),
                    "-o", "StrictHostKeyChecking=yes", "-o", "UserKnownHostsFile=" + tempFile);
            if (debugSsh) {
                builder.commonArg("-v");
            }
            scptry = builder.build();
        } catch (NEPException nepException) {
            log.debug(nepException.getMessage());
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
            Arrays.asList("/usr/bin/sshpass", "/opt/local/bin/sshpass", "/usr/local/bin/sshpass"/*brew*/),
            3,
            false
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
        @NonNull String directory,
        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout,
        Function<FileMetadata, Proceed> descriptorConsumer) {
        int exitCode = 0;
        String url = getUrl(directory, nepFile);
        int tryNumber = 0;
        RuntimeException catchedException;
        do {
            catchedException = null;
            if (tryNumber > 0) {
                try {
                    Thread.sleep(waitBetweenRetries.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            try {
                checkAvailability(directory, nepFile, timeout, descriptorConsumer);

                try (OutputStream out = outputStream.get()) {
                    if (out != null) {
                        log.info("Copying {} to {}", url, out);
                        exitCode = scp.execute(out,
                            LoggerOutputStream.log(log, l -> {
                                    if (StringUtils.isBlank(l)) {
                                        return null;
                                    }
                                    if (l.contains("Warning")) {
                                        return Level.WARN;
                                    }
                                    return Level.ERROR;
                                }
                            ), url, "/dev/stdout");
                    } else {
                        log.warn("Can't download from {} stream to null", url);
                    }
                }
            } catch (InterruptedException e) {
                log.error(e.getClass().getName() + ":" + e.getMessage(), e);
                Thread.currentThread().interrupt();
                return;
            } catch (IOException  e) {
                log.error(e.getClass().getName() + ":" + e.getMessage(), e);
                return;
            } catch (CommandExecutor.BrokenPipe | NEPException bp) {
                log.debug(bp.getMessage());
                throw bp;
            } catch (RuntimeException rte) {
                log.warn(rte.getClass().getName() + ":" + rte.getMessage());
                catchedException = rte;
                exitCode = -100;
            }
            if (exitCode == 0) {
                return;
            } else {
                log.warn("SCP command  from " + url + " failed with exitcode {}. Will try again in {}", exitCode, waitBetweenRetries);
            }
        } while (tryNumber++ < maxDownloadRetries);

        if (catchedException != null) {
            log.error(catchedException.getMessage(), catchedException);

            throw catchedException;
        }
        throw new CommandExecutor.ExitCodeException("SCP command  from " + url + " failed", exitCode);

    }

    @Override
    public String getDownloadString() {
        return url + ":";

    }

    protected String getUrl(@NonNull String directory, String nepFile) {
        return url + ":" + NEPDownloadService.join(directory, nepFile);
    }


    protected void checkAvailability(
        @NonNull String directory,
        @NonNull String nepFile,
        @Nullable Duration timeout,
        @NonNull Function<FileMetadata, Proceed> descriptorConsumer) throws IOException, InterruptedException {
        sshj.checkAvailabilityAndConsume(directory, nepFile, timeout, descriptorConsumer, (handle) -> {});
    }

    @Override
    public String toString () {
        return getClass().getSimpleName() + ":" + scp + " " + getDownloadString();
    }



}
