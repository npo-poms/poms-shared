package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.CommandExecutor;
import nl.vpro.util.CommandExecutorImpl;
import nl.vpro.util.FileMetadata;

/**
 * See MSE-4032. It's kind of a disgrace that we have to fall back to external commands...
 *
 * osx: sudo port install curl +sftp_scp (or use brew)
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
//@Named("NEPDownloadService")
@Slf4j
public class NEPCurlDownloadServiceImpl implements NEPDownloadService {


    private final String ftpHost;
    private final CommandExecutor curl;
    private final NEPSSJDownloadServiceImpl sshj;


    public NEPCurlDownloadServiceImpl(
        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostkey
    ) {
        this.ftpHost = ftpHost;
        String user = username + ":" + password;

        curl = CommandExecutorImpl.builder()
            .executablesPaths(
                "/usr/local/opt/curl/bin/curl", // brew
                "/opt/local/bin/curl", // macports
                "/usr/bin/curl" // linux
            )
            .wrapLogInfo((message) -> message.replaceAll(password, "??????"))
            .useFileCache(true)
            .commonArgs(Arrays.asList("-s", "-u", user, "--insecure"))
            .build();
        // just used for the checkAvailability call (actually for the descriptorConsumer callback)
        sshj = new NEPSSJDownloadServiceImpl(ftpHost, username, password, hostkey);
    }

    @Override
    public void download(
        @Nonnull  String nepFile, @Nonnull Supplier<OutputStream> outputStream, @Nonnull Duration timeout, Function<FileMetadata, Boolean> descriptorConsumer) {
        try {
            checkAvailability(nepFile, timeout, descriptorConsumer);
            if (outputStream != null) {
                try (OutputStream out = outputStream.get()) {
                    curl.execute(out, getUrl(nepFile));
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
        return "sftp://" + ftpHost + "/" + nepFile;
    }


    protected void checkAvailability(String nepFile, Duration timeout,  Function<FileMetadata, Boolean> descriptorConsumer) throws IOException {
        sshj.checkAvailabilityAndConsume(nepFile, timeout, descriptorConsumer, (handle) -> {});

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
