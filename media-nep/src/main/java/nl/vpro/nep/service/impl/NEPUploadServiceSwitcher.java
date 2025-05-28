package nl.vpro.nep.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.mediainfo.MediaInfo;
import nl.vpro.nep.service.NEPUploadService;

/**
 * @since 8.10
 */
public class NEPUploadServiceSwitcher implements NEPUploadService {

    final NEPUploadService nepftpUploadService;
    final NEPUploadService nepftpUploadVerticalService;

    final MediaInfo mediaInfoCaller = new MediaInfo();

    public NEPUploadServiceSwitcher(
        NEPUploadService nepftpUploadService,
        NEPUploadService nepftpUploadVerticalService
    ) {
        this.nepftpUploadService = nepftpUploadService;
        this.nepftpUploadVerticalService = nepftpUploadVerticalService;
    }


    @Override
    public long upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull Path incomingFile, boolean replaces) throws IOException {
        MediaInfo.Result mediaInfo = mediaInfoCaller.apply(incomingFile);
        logger.info("Mediainfo for {}: {}", incomingFile, mediaInfo);
        if (mediaInfo.vertical()) {
            logger.info("Using vertical upload service for {}", nepFile);
            return nepftpUploadVerticalService.upload(logger, nepFile, size, incomingFile, replaces);
        } else {
            logger.info("Using standard upload service for {}", nepFile);
            return nepftpUploadService.upload(logger, nepFile, size, incomingFile, replaces);
        }
    }

    @Override
    public long upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull InputStream stream, boolean replaces) throws IOException {
        throw new UnsupportedOperationException("This method is not implemented in NEPUploadServiceSwitcher");
    }

    @Override
    public String getUploadString() {
        return nepftpUploadService.getUploadString() + "/" + nepftpUploadVerticalService.getUploadString();
    }
}
