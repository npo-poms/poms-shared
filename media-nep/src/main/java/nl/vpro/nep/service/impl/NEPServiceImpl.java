package nl.vpro.nep.service.impl;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.domain.*;
import nl.vpro.nep.domain.workflow.*;
import nl.vpro.nep.service.*;
import nl.vpro.util.FileMetadata;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPService")
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
        @Named("NEPTokenService") Provider<NEPPlayerTokenService> tokenService
        ) {
        this.gatekeeperService = gatekeeperService;
        this.nepftpUploadService = nepftpUploadService;
        this.nepftpDownloadService = nepftpDownloadService;
        this.itemizeService = itemizeService;
        this.samService = samService;
        this.tokenService = tokenService;
    }

    @Override
    public NEPItemizeResponse itemize(String channel, Instant start, Instant stop, Integer max_bitrate) {
        return itemizeService.get().itemize(channel, start, stop, max_bitrate);
    }

    @Override
    public void grabScreen(String channel, Instant instant, OutputStream outputStream) {
        itemizeService.get().grabScreen(channel, instant, outputStream);
    }

    @Override
    public NEPItemizeResponse itemize(String mid, Duration start, Duration stop, Integer max_bitrate) {
        return itemizeService.get().itemize(mid, start, stop, max_bitrate);

    }

    @Override
    public void grabScreen(String mid, Duration offset, OutputStream outputStream) {
        itemizeService.get().grabScreen(mid, offset, outputStream);
    }

    @NonNull
    @Override
    public WorkflowExecution transcode(
        @NonNull WorkflowExecutionRequest request) throws IOException {
        return gatekeeperService.get().transcode(request);
    }

    @NonNull
    @Override
    public Iterator<WorkflowExecution> getTranscodeStatuses(String mid, StatusType status, Instant from, Long limit) {
        return gatekeeperService.get().getTranscodeStatuses(mid, status, from, limit);
    }

    @Override
    public @NonNull Optional<WorkflowExecution> getTranscodeStatus(@NonNull  String workflowId) {
        return gatekeeperService.get().getTranscodeStatus(workflowId);
    }

    @Override
    public void download(
        @NonNull String nepFile,
        @NonNull Supplier<OutputStream> outputStream,
        @NonNull Duration timeout,
        @Nullable Function<FileMetadata, Proceed> descriptorConsumer) throws IOException {
        nepftpDownloadService.get()
            .download(nepFile, outputStream, timeout, descriptorConsumer);
    }

    @Override
    public long upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull InputStream stream, boolean replaces) throws IOException {
        return nepftpUploadService.get().upload(logger, nepFile, size, stream, replaces);
    }

    @Override
    public WideVineResponse widevineToken(String ip) {
        return tokenService.get().widevineToken(ip);
    }

    @Override
    public PlayreadyResponse playreadyToken(String ip) {
        return tokenService.get().playreadyToken(ip);
    }

    @Override
    public String streamAccessLive(String channel, String ip, Duration duration) {
        return samService.get().streamAccessLive(channel, ip, duration);
    }

    @Override
    public String streamAccessMid(String mid, boolean drm, String ip, Duration duration) {
        return samService.get().streamAccessMid(mid, drm, ip, duration);
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

}
