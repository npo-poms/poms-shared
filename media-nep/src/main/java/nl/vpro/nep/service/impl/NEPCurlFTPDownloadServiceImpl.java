package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

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
public class NEPCurlFTPDownloadServiceImpl implements NEPDownloadService {


    private final String ftpHost;
    private final CommandExecutor curl;

    public NEPCurlFTPDownloadServiceImpl(
        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostkey
    ) {
        this.ftpHost = ftpHost;
        String user = username + ":" + password;
        // TODO avoid --insecure
        /*File pemFile = File.createTempFile(ftpHost, ".pem");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pemFile)));
        writer.println("-----BEGIN CERTIFICATE-----");
        writer.println(hostkey);
        writer.println("-----END CERTIFICATE-----");
        writer.close();
*/
        curl = CommandExecutorImpl.builder()
            .executablesPaths("/usr/local/opt/curl/bin/curl", "/usr/bin/curl")
            .wrapLogInfo((message) -> message.replaceAll(password, "??????"))
            .commonArgs(Arrays.<String>asList("-s", "-u", user, "--insecure"))
            .build();
    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        checkAvailability(nepFile, timeout, descriptorConsumer);
        try {
            CompletableFuture<Integer> submitted = curl.submit(outputStream, "sftp://" + ftpHost + "/" + nepFile);
            submitted.exceptionally((e) -> {
                log.error(e.getMessage(), e);
                return -1;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }


    }

    protected void checkAvailability(String nepFile, Duration timeout,  Function<FileDescriptor, Boolean> descriptorConsumer) {
          Instant start = Instant.now();

        while(true) {
            StringWriter writer = new StringWriter();
            curl.execute(writer, "-I", "sftp://" + ftpHost + "/" + nepFile);
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
                 if (timeout == null || timeout.equals(Duration.ZERO)) {
                     throw new IllegalStateException("File " + nepFile + " doesn't exist");
                 }
                if (Duration.between(start, Instant.now()).compareTo(timeout) > 0) {
                    throw new IllegalStateException("File " + nepFile + " didn't appear in " + timeout);
                }
            } else {
                if (descriptorConsumer != null) {
                    descriptorConsumer.apply(descriptor);
                }
                break;
            }
        }
    }
}
