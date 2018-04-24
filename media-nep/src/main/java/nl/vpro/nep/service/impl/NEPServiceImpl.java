package nl.vpro.nep.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.springframework.stereotype.Service;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;
import nl.vpro.nep.service.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Service
public class NEPServiceImpl implements NEPService {
    private final Provider<TranscodeService> transcodeService;
    private final Provider<NEPFTPUploadService> nepftpUploadService;
    private final Provider<NEPFTPDownloadService> nepftpDownloadService;
    private final Provider<ItemizeService> itemizeService;



    @Inject
    public NEPServiceImpl(
        @Named("TranscodeService") Provider<TranscodeService> transcodeService,
        @Named("NEPFTPUploadService") Provider<NEPFTPUploadService> nepftpUploadService,
        @Named("NEPFTPDownloadService") Provider<NEPFTPDownloadService> nepftpDownloadService,
        @Named("ItemizeService") Provider<ItemizeService> itemizeService

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

    @Override
    public WorkflowExecution transcode(WorkflowExecutionRequest request) throws IOException {
        return transcodeService.get().transcode(request);

    }

    @Override
    public Iterator<WorkflowExecution> getTranscodeStatuses(String mid, StatusType status, Instant from, Long limit) {
        return transcodeService.get().getTranscodeStatuses(mid, status, from, limit);

    }

    @Override
    public void download(String nepFile, OutputStream outputStream, Duration timeout, Function<FileDescriptor, Boolean> descriptorConsumer) throws IOException {
        nepftpDownloadService.get().download(nepFile, outputStream, timeout, descriptorConsumer);

    }

    @Override
    public long upload(SimpleLogger logger, String nepFile, Long size, InputStream stream) throws IOException {
        return nepftpUploadService.get().upload(logger, nepFile, size, stream);

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
