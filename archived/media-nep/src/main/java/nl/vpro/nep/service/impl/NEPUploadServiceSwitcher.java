package nl.vpro.nep.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.mediainfo.MediaInfo;
import nl.vpro.mediainfo.MediaInfoService;
import nl.vpro.nep.service.NEPUploadService;

import static nl.vpro.i18n.MultiLanguageString.en;
import static nl.vpro.poms.shared.UploadUtils.Phase.*;
import static nl.vpro.poms.shared.UploadUtils.setPhase;

/**
 * @since 8.10
 */
public class NEPUploadServiceSwitcher implements NEPUploadService {

    final NEPUploadService nepftpUploadService;
    final NEPUploadService nepftpUploadVerticalService;

    final MediaInfoService mediainfoService;

    public NEPUploadServiceSwitcher(
        MediaInfoService mediainfoService,
        NEPUploadService nepftpUploadService,
        NEPUploadService nepftpUploadVerticalService
    ) {
        this.mediainfoService = mediainfoService;
        this.nepftpUploadService = nepftpUploadService;
        this.nepftpUploadVerticalService = nepftpUploadVerticalService;
    }


    @Override
    public UploadResult upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull Path incomingFile, boolean replaces)  {
        setPhase(mediainfo);

        MediaInfo mediaInfo = mediainfoService.apply(incomingFile);
        logger.info(
            en("Mediainfo for %s: %s")
                .nl("Mediainformatie voor %s: %s")
                .formatted(nepFile, mediaInfo).build());
        setPhase(mediainfo_conclusion);

        final  NEPUploadService service;

        if (mediaInfo.vertical()) {
            if (!nepftpUploadVerticalService.isUploadEnabled()) {
                throw new IllegalStateException("Vertical upload to NEP was never properly implemented. And this could should ot have been reached.");
            }
            logger.info(
                en("Using vertical upload service for %s")
                    .nl("Gebruik NEP-uploadservice voor verticale video voor %s")
                    .formatted(nepFile).build());
            service = nepftpUploadVerticalService;
        } else {
            logger.info(en("Using classic NEP upload service for %s")
                    .nl("Gebruik klassieke NEP-uploadservice voor %s")
                    .formatted(nepFile).build());
            service = nepftpUploadService;
        }
        try {
            setPhase(uploading);
            return service.upload(logger, nepFile, size, incomingFile, replaces).withMediaInfo(mediaInfo);
        } catch (IOException e) {
            logger.error("Error uploading {} with {} : {}", nepFile, service, e.getMessage(), e);
            return new UploadResult(size, service.toString(),
                e.getMessage(),
                mediaInfo);

        }
    }

    @Override
    public long upload(@NonNull SimpleLogger logger, @NonNull String nepFile, @NonNull Long size, @NonNull InputStream stream, boolean replaces) throws IOException {
        throw new UnsupportedOperationException("This method is not implemented in NEPUploadServiceSwitcher");
    }

    @Override
    public String getUploadString() {
        return Stream.of(nepftpUploadService, nepftpUploadVerticalService)
            .filter(NEPUploadService::isUploadEnabled)
            .map(NEPUploadService::getUploadString)
            .collect(Collectors.joining("+"));
    }

    @Override
    public boolean isUploadEnabled() {
        return nepftpUploadService.isUploadEnabled() || nepftpUploadVerticalService.isUploadEnabled();
    }
}
