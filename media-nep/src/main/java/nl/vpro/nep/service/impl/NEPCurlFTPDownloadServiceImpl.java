package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import org.apache.http.client.utils.DateUtils;
import org.slf4j.ext.LoggerWrapper;
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
@Slf4j
public class NEPCurlFTPDownloadServiceImpl implements NEPDownloadService {


    private final String ftpHost;
    private final String user;
    private final String password;
    private final CommandExecutor CURL = CommandExecutorImpl.builder()
        .executable(new File("/usr/bin/curl"))
        .logger(new LoggerWrapper(log, log.getName()) {
            @Override
            public void info(String message) {
                super.info(message.replaceAll(password, "??????"));
            }
        })
        .build();

    public NEPCurlFTPDownloadServiceImpl(
        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password
    ) {
        this.ftpHost = ftpHost;
        this.user = username + ":" + password;
        this.password = password;
    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        checkAvailability(nepFile, timeout, descriptorConsumer);
        CURL.execute(outputStream, "-s", "-u", user , "ftp://" + ftpHost + "/" + nepFile);


    }

    protected void checkAvailability(String nepFile, Duration timeout,  Function<FileDescriptor, Boolean> descriptorConsumer) {
          Instant start = Instant.now();

        while(true) {
            StringWriter writer = new StringWriter();
            CURL.execute(writer, "-I", "-s", "-u", user , "ftp://" + ftpHost + "/" + nepFile);
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
