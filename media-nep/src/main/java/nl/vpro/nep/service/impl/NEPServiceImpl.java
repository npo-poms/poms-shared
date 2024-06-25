package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.*;

import jakarta.annotation.PreDestroy;
import jakarta.inject.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.domain.*;
import nl.vpro.nep.domain.workflow.*;
import nl.vpro.nep.service.*;
import nl.vpro.nep.service.exception.NEPException;
import nl.vpro.util.FileMetadata;

/**
 * Implements all available NEP services (via {@link NEPService}. Also,  it may add rate capping. (See MSE-5795).
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPService")
@Slf4j
public class NEPServiceImpl implements NEPService {

    private final Provider<NEPGatekeeperService> gatekeeperService;
    private final Provider<NEPUploadService> nepftpUploadService;
    private final Provider<NEPDownloadService> nepftpDownloadService;
    private final Provider<NEPItemizeService> itemizeService;
    private final Provider<NEPSAMService> samService;
    private final Provider<NEPPlayerTokenService> tokenService;

    @Inject
    public NEPServiceImpl(
        @Named("NEPGatekeeperService") Provider<NEPGatekeeperService> gatekeeperService,
        @Named("NEPUploadService") Provider<NEPUploadService> nepftpUploadService,
        @Named("NEPDownloadService") Provider<NEPDownloadService> nepftpDownloadService,
        @Named("NEPItemizeService") Provider<NEPItemizeService> itemizeService,
        @Named("NEPSAMService") Provider<NEPSAMService> samService,
        @Named("NEPTokenService") Provider<NEPPlayerTokenService> tokenService     ) {
        this.gatekeeperService = gatekeeperService;
        this.nepftpUploadService = nepftpUploadService;
        this.nepftpDownloadService = nepftpDownloadService;
        this.itemizeService = itemizeService;
        this.samService = samService;
        this.tokenService = tokenService;
    }

    @Override
    public NEPItemizeResponse itemizeLive(String channel, Instant start, Instant stop, Integer max_bitrate) throws NEPException {
        return itemizeService.get().itemizeLive(channel, start, stop, max_bitrate);
    }

    @Override
    public void grabScreenLive(String channel, Instant instant, BiConsumer<String, String> headers, OutputStream outputStream) throws NEPException {
        itemizeService.get().grabScreenLive(channel, instant, headers, outputStream);
    }

    @Override
    public String getLiveItemizerString() {
        return itemizeService.get().getLiveItemizerString();

    }

    @Override
    public String getMidItemizerString() {
        return itemizeService.get().getMidItemizerString();
    }

    @Override
    public ItemizerStatusResponse getLiveItemizerJobStatus(String jobId) {
        return itemizeService.get().getLiveItemizerJobStatus(jobId);
    }

    @Override
    public ItemizerStatusResponse getMidItemizerJobStatus(String jobId) {
        return itemizeService.get().getMidItemizerJobStatus(jobId);
    }


    @Override
    public NEPItemizeResponse itemizeMid(String mid, Duration start, Duration stop, Integer max_bitrate) throws NEPException {
        return itemizeService.get().itemizeMid(mid, start, stop, max_bitrate);
    }

    @Override
    public void grabScreenMid(String mid, Duration offset, BiConsumer<String, String> headers, OutputStream outputStream) throws NEPException {
        itemizeService.get().grabScreenMid(mid, offset, headers, outputStream);
    }

    @NonNull
    @Override
    public WorkflowExecution transcode(
        @NonNull WorkflowExecutionRequest request) throws IOException {
        return gatekeeperService.get().transcode(request);
    }

    @NonNull
    @Override
    public Iterator<WorkflowExecution> getTranscodeStatuses(String mid, StatusType status, Instant from, Long limit) throws NEPException {
        return gatekeeperService.get().getTranscodeStatuses(mid, status, from, limit);
    }

    @Override
    public @NonNull Optional<WorkflowExecution> getTranscodeStatus(@NonNull  String workflowId) throws NEPException {
        return gatekeeperService.get().getTranscodeStatus(workflowId);
    }

    @Override
    public String getGatekeeperString() {
        return gatekeeperService.get().getGatekeeperString();
    }

    @Override
    public void download(
        @NonNull String directory,

        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout,
        @Nullable Function<FileMetadata, Proceed> descriptorConsumer) throws IOException {
        nepftpDownloadService.get()
            .download(directory, nepFile, outputStream, timeout, descriptorConsumer);
    }

    @Override
    public String getDownloadString() {
        return nepftpDownloadService.get().getDownloadString();

    }

    @Override
    public long upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull InputStream stream, boolean replaces) throws IOException {
        return nepftpUploadService.get().upload(logger, nepFile, size, stream, replaces);
    }

    @Override
    public long upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull Path stream, boolean replaces) throws IOException {
        return nepftpUploadService.get().upload(logger, nepFile, size, stream, replaces);
    }


    @Override
    public String getUploadString() {
        return nepftpUploadService.get().getUploadString();

    }

    @Override
    public WideVineResponse widevineToken(String ip) throws NEPException {
        return tokenService.get().widevineToken(ip);
    }

    @Override
    public PlayreadyResponse playreadyToken(String ip) throws NEPException {
        return tokenService.get().playreadyToken(ip);
    }

    @Override
    public FairplayResponse fairplayToken(String ip) throws NEPException {
        return tokenService.get().fairplayToken(ip);
    }

    @Override
    public String getPlayerTokenString() {
        return tokenService.get().getPlayerTokenString();
    }

    @Override
    public Optional<String> streamAccessLive(String channel, String ip, Duration duration) throws NEPException {
        return samService.get().streamAccessLive(channel, ip, duration);
    }

    @Override
    public Optional<String> streamAccessMid(String mid, boolean drm, String ip, Duration duration) throws NEPException {
        return samService.get().streamAccessMid(mid, drm, ip, duration);
    }

    @Override
    public String getStreamAccessLiveString() {
        return samService.get().getStreamAccessLiveString();
    }

    @Override
    public String getStreamAccessMidString() {
        return samService.get().getStreamAccessMidString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NEP: ");
        try {
            builder.append("itemizer:").append(itemizeService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        try {
            builder.append("gatekeeper:").append(gatekeeperService.get().toString()).append(",");
        } catch (Exception ignored) {

        }

        try {
            builder.append("upload:").append(nepftpUploadService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        try {
            builder.append("download:").append(nepftpDownloadService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        try {
            builder.append("sam:").append(samService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
           try {
               builder.append("tokens:").append(tokenService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        return builder.toString();
    }

    @Override
    @PreDestroy
    public void close() throws Exception {
        closeQuietly(
            gatekeeperService,
            itemizeService,
            samService,
            tokenService
        );
    }

    @SafeVarargs
    private static void closeQuietly(Provider<? extends AutoCloseable>... closeables) {
        for (Provider<? extends AutoCloseable> closeable : closeables) {
            try {
                closeable.get().close();
            } catch (Exception e) {
                log.debug(e.getMessage());
            }

        }
    }
}
