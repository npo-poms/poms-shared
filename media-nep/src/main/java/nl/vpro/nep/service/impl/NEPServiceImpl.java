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
import nl.vpro.nep.sam.model.StreamAccessItem;
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
    public NEPItemizeResponse itemizeLive(String channel, Instant start, Instant stop, Integer max_bitrate) {
        return itemizeService.get().itemizeLive(channel, start, stop, max_bitrate);

    }

    @Override
    public NEPItemizeResponse itemizeMid(String mid, Duration start, Duration stop, Integer max_bitrate) {
        return itemizeService.get().itemizeMid(mid, start, stop, max_bitrate);

    }

    @Override
    public void grabScreen(String identifier, String date, OutputStream outputStream) {
        itemizeService.get().grabScreen(identifier, date, outputStream);
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
    public String streamAccess(String streamId, boolean drm, StreamAccessItem streamUrlRequest) {
        return samService.get().streamAccess(streamId, drm, streamUrlRequest);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NEP: ");
        try {
            builder.append("itemizer: ").append(itemizeService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        try {
            builder.append("workflows: ").append(gatekeeperService.get().toString()).append(",");
        } catch (Exception ignored) {

        }

        try {
            builder.append("upload: ").append(nepftpUploadService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        try {
            builder.append("download: ").append(nepftpDownloadService.get().toString()).append(",");
        } catch (Exception ignored) {

        }
        return builder.toString();
    }

}
