package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import jakarta.inject.Inject;

import org.apache.http.client.utils.DateUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.CommandExecutor;
import nl.vpro.util.CommandExecutorImpl;
import nl.vpro.util.FileMetadata;

/**
 * See MSE-4032. It's kind of a disgrace that we have to fall back to external commands...
 * <p>
 * osx: sudo port install curl +sftp_scp (or use brew)
 * <p>
 * Doesn't work either because of 'rekeying'? See {@link NEPSSHJUploadServiceImpl}.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
//@Named("NEPDownloadService")
@Slf4j
public class NEPCurlDownloadServiceImpl implements NEPDownloadService {

    private final String ftpHost;
    private final CommandExecutor curl;
    private final NEPSSHJDownloadServiceImpl sshj;

    @Inject
    public NEPCurlDownloadServiceImpl(
        @Value("${nep.itemizer-download.host}") String ftpHost,
        @Value("${nep.itemizer-download.username}") String username,
        @Value("${nep.itemizer-download.password}") String password,
        @Value("${nep.itemizer-download.hostkey}") String hostkey,
        @Value("${executables.curl}") List<String> executables
    ) {
        this.ftpHost = ftpHost;
        String user = username + ":" + password;

        curl = CommandExecutorImpl.builder()
            .executablesPaths(executables)
            .wrapLogInfo((message) -> message.toString().replaceAll(password, "??????"))
            .useFileCache(true)
            .commonArgs(Arrays.asList("-s", "-u", user, "--insecure"))
            .build();
        // just used for the checkAvailability call (actually for the descriptorConsumer callback)
        sshj = new NEPSSHJDownloadServiceImpl(ftpHost, username, password, hostkey);

    }

    @Override
    public void download(
        @NonNull String directory,
        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout,
        @Nullable Function<FileMetadata, Proceed> descriptorConsumer) {
        int exitCode = 0;
        try {
            checkAvailability(directory, nepFile, timeout, descriptorConsumer);
            if (outputStream != null) {
                try (OutputStream out = outputStream.get()) {
                    exitCode = curl.execute(out, getUrl(nepFile));
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (CommandExecutor.BrokenPipe bp) {
            log.debug(bp.getMessage());
            throw bp;
        } catch (RuntimeException rte) {
            log.error(rte.getMessage(), rte);
            throw rte;
        }
        if (exitCode != 0) {
            throw new CommandExecutor.ExitCodeException("Curl call failed", exitCode);
        }
    }

    @Override
    public String getDownloadString() {
        return "curl:" + ftpHost;

    }

    protected String getUrl(String nepFile) {
        return "sftp://" + ftpHost + "/" + nepFile;
    }

    protected void checkAvailability(String directory, String nepFile, Duration timeout,  Function<FileMetadata, Proceed> descriptorConsumer) throws IOException, InterruptedException {
        sshj.checkAvailabilityAndConsume(directory, nepFile, timeout, descriptorConsumer, (handle) -> {});
    }

    /**
     * What the fuck, doesn't work with sftp
     */
    protected void checkAvailabilityWithCurl(String nepFile, Duration timeout,  Function<FileMetadata, Boolean> descriptorConsumer) throws InterruptedException {
        Instant start = Instant.now();
        while(true) {
            StringWriter writer = new StringWriter();
            int result = curl.execute(writer, "-I", getUrl(nepFile));
            log.info("Result {}", result);
            if (result == 0) {
                FileMetadata.Builder descriptorBuilder = FileMetadata.builder().fileName(nepFile);
                for (String l : writer.toString().split("\\n")) {
                    String[] split = l.split(":", 2);
                    if (split[0].equalsIgnoreCase("Last-Modified")) {
                        descriptorBuilder.lastModified(nl.vpro.util.DateUtils.toInstant(DateUtils.parseDate(split[1].trim())));
                    }
                    if (split[0].equalsIgnoreCase("Content-Length")) {
                        descriptorBuilder.size(Long.parseLong(split[1].trim()));
                    }
                }
                FileMetadata descriptor = descriptorBuilder.build();
                if (descriptor.getSize() == null) {
                    log.warn("No size found in output of curl -I, for {}", getUrl(nepFile));
                }
                if (descriptorConsumer != null) {
                    descriptorConsumer.apply(descriptor);
                }
                break;
            } else {
                if (Duration.between(start, Instant.now()).compareTo(timeout) > 0) {
                    throw new IllegalStateException("File " + nepFile + " didn't appear in " + timeout);
                }
                Thread.sleep(10000L);
            }
        }
    }
}
