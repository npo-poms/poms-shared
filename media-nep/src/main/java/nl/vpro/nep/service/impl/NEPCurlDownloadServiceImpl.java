package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.RemoteFile;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.inject.Named;

import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.service.FileDescriptor;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.CommandExecutor;
import nl.vpro.util.CommandExecutorImpl;

/**
 * See MSE-4032. It's kind of a disgrace that we have to fall back to external commands...
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Named("NEPDownloadService")
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
            .executablesPaths("/usr/local/opt/curl/bin/curl", "/usr/bin/curl")
            .wrapLogInfo((message) -> message.replaceAll(password, "??????"))
            .commonArgs(Arrays.<String>asList("-s", "-u", user, "--insecure"))
            .build();
        sshj = new NEPSSJDownloadServiceImpl(ftpHost, username, password, hostkey);
    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        try {
            checkAvailability(nepFile, timeout, descriptorConsumer);
            CompletableFuture<Integer> submitted = curl.submit(outputStream, getUrl(nepFile));
            submitted.exceptionally((e) -> {
                log.error(e.getMessage(), e);
                return -1;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }

    }

    protected String getUrl(String nepFile) {
        return "sftp://" + ftpHost + "/" + nepFile;
    }


    protected void checkAvailability(String nepFile, Duration timeout,  Function<FileDescriptor, Boolean> descriptorConsumer) {
        try(
            RemoteFile handle = sshj.checkAvailability(nepFile, timeout, descriptorConsumer);
            ) {
        } catch (InterruptedException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * What the fuck, doesn't work with sftp
     */
    protected void checkAvailabilityWithCurl(String nepFile, Duration timeout,  Function<FileDescriptor, Boolean> descriptorConsumer) throws InterruptedException {
          Instant start = Instant.now();

        while(true) {
            StringWriter writer = new StringWriter();
            int result = curl.execute(writer, "-I", getUrl(nepFile));
            log.info("Result {}", result);
            if (result == 0) {
                FileDescriptor.Builder descriptorBuilder = FileDescriptor.builder().fileName(nepFile);
                for (String l : writer.toString().split("\\n")) {
                    String[] split = l.split(":", 2);
                    if (split[0].equalsIgnoreCase("Last-Modified")) {
                        descriptorBuilder.lastModified(nl.vpro.util.DateUtils.toInstant(DateUtils.parseDate(split[1].trim())));
                    }
                    if (split[0].equalsIgnoreCase("Content-Length")) {
                        descriptorBuilder.size(Long.parseLong(split[1].trim()));
                    }
                }
                FileDescriptor descriptor = descriptorBuilder.build();
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
