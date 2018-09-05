package nl.vpro.nep.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;
import nl.vpro.nep.service.*;
import nl.vpro.util.FileMetadata;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPService")
public class NEPServiceImpl implements NEPService {
    private final Provider<NEPTranscodeService> transcodeService;
    private final Provider<NEPUploadService> nepftpUploadService;
    private final Provider<NEPDownloadService> nepftpDownloadService;
    private final Provider<NEPItemizeService> itemizeService;



    @Inject
    public NEPServiceImpl(
        @Named("NEPTranscodeService") Provider<NEPTranscodeService> transcodeService,
        @Named("NEPUploadService") Provider<NEPUploadService> nepftpUploadService,
        @Named("NEPDownloadService") Provider<NEPDownloadService> nepftpDownloadService,
        @Named("NEPItemizeService") Provider<NEPItemizeService> itemizeService

        ) {
        this.transcodeService = transcodeService;
        this.nepftpUploadService = nepftpUploadService;
        this.nepftpDownloadService = nepftpDownloadService;
        this.itemizeService = itemizeService;
    }

    @Override
    public NEPItemizeResponse itemize(NEPItemizeRequest request) {
        return itemizeService.get().itemize(request);
    }

    @Nonnull
    @Override
    public WorkflowExecution transcode(WorkflowExecutionRequest request) throws IOException {
        return transcodeService.get().transcode(request);

    }

    @Nonnull
    @Override
    public Iterator<WorkflowExecution> getTranscodeStatuses(String mid, StatusType status, Instant from, Long limit) {
        return transcodeService.get().getTranscodeStatuses(mid, status, from, limit);

    }

    @Override
    public void download(
        @Nonnull String nepFile,
        @Nonnull Supplier<OutputStream> outputStream,
        @Nonnull Duration timeout,
        @Nullable  Function<FileMetadata, Proceed> descriptorConsumer) throws IOException {
        nepftpDownloadService.get()
            .download(nepFile, outputStream, timeout, descriptorConsumer);

    }

    @Override
    public long upload(@Nonnull SimpleLogger logger, @Nonnull String nepFile, @Nonnull Long size, @Nonnull InputStream stream, boolean replaces) throws IOException {
        return nepftpUploadService.get().upload(logger, nepFile, size, stream, replaces);

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
            builder.append("workflows: ").append(transcodeService.get().toString()).append(",");
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
