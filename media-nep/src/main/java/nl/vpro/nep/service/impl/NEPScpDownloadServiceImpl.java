package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;

import javax.inject.Named;

import org.springframework.beans.factory.annotation.Value;

import nl.vpro.nep.service.FileDescriptor;
import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.CommandExecutor;
import nl.vpro.util.CommandExecutorImpl;

/**
 * See MSE-4032. It's kind of a disgrace that we have to fall back to external commands...
 *
 * I first tried curl, to no avail either.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Named("NEPDownloadService")
@Slf4j
public class NEPScpDownloadServiceImpl implements NEPDownloadService {


    private final String url;
    private final CommandExecutor scp;
    private final NEPSSJDownloadServiceImpl sshj;


    public NEPScpDownloadServiceImpl(
        @Value("${nep.sftp.host}") String ftpHost,
        @Value("${nep.sftp.username}") String username,
        @Value("${nep.sftp.password}") String password,
        @Value("${nep.sftp.hostkey}") String hostkey
    ) {
        this.url = username + "@" + ftpHost;
        File scpcommand = CommandExecutorImpl.getExecutable("/usr/bin/scp").orElseThrow(IllegalArgumentException::new);
        sshj = new NEPSSJDownloadServiceImpl(ftpHost, username, password, hostkey);
        CommandExecutor scptry = null;
        try {
            scptry = CommandExecutorImpl.builder()
                .executablesPaths("/usr/bin/sshpass", "/opt/local/bin/sshpass")
                .wrapLogInfo((message) -> message.replaceAll(password, "??????"))
                //.useFileCache(true)
                .commonArgs(Arrays.asList("-p", password, scpcommand.getAbsolutePath(), "-o", "StrictHostKeyChecking=no"))
                .build();
            // just used for the checkAvailability call (actually for the descriptorConsumer callback)

        } catch (RuntimeException rte) {
            log.error(rte.getMessage(), rte);
        }
        scp = scptry;
    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) {
        try {
            checkAvailability(nepFile, timeout, descriptorConsumer);
            scp.execute(outputStream, getUrl(nepFile), "/dev/stdout");
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


    protected void checkAvailability(String nepFile, Duration timeout,  Function<FileDescriptor, Boolean> descriptorConsumer) throws IOException {
        sshj.checkAvailabilityAndConsume(nepFile, timeout, descriptorConsumer, (handle) -> {});

    }

}
