package nl.vpro.poms.shared;

import java.util.function.Consumer;

import org.slf4j.MDC;

import nl.vpro.i18n.Displayable;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.i18n.MultiLanguageString.en;

/**
 * @since 8.10 (Used to be in SourcingService)
 */
public class UploadUtils {

    public enum Phase implements Displayable {
        /**
         * Receiving from client is busy
         */
        receiving,
        /**
         * Receiving from client is done.
         */
        receiving_done,
        /**
         * Determining media info about the received asset now
         */
        mediainfo,
        /**
         * Determining media info about the received asset is done now
         */
        mediainfo_conclusion,
        /**
         * Announcing the upload. Making prediction in poms.
         */
        announcing,

        /**
         * Now uploading the received asset (ftp to NEP)
         */
        uploading,
        /**
         * Other actions in preparation for transcoding
         */
        transcode_preparing,

        /**
         * Receiving/uploading all done, and transcoding is requested
         */
        transcode_requested
        ;

        @Override
        public String getDisplayName() {
            return name();
        }
    }

    /**
     * The current phase of the upload, used to log the phase in which the upload is.
     * This is also included in the id of the html message that is sent to the GUI.
     */
    private static final ThreadLocal<Phase> PHASE = ThreadLocal.withInitial(() -> null);

    private static final Consumer<Phase> SET_PHASE = PHASE::set;

    public static final ThreadLocal<Consumer<Phase>> PHASE_LISTENER = ThreadLocal.withInitial(() -> SET_PHASE);

    public static void setPhase(Phase phase) {
        PHASE_LISTENER.get().accept(phase);
        MDC.put("uploadPhase", phase.name());
    }

    public static Phase getPhase() {
        return PHASE.get();
    }
    public static String getPhaseName() {
        Phase phase =  PHASE.get();
        return phase == null ? "" : phase.name();
    }

    public static void remove() {
        PHASE.remove();
        PHASE_LISTENER.remove();
        MDC.remove("uploadPhase");
    }

    /**
     * a Consumer for {@link FileCachingInputStream} which logs progress to the logger, interpreting the inputstream as 'receive'.
     */
    public static Consumer<FileCachingInputStream> loggingConsumer(final SimpleLogger logger, String impl) {
        return fci -> {
            if (fci.isReady()) {
                if (fci.getException().isEmpty()) {
                    logger.info(en("Received {} {}")
                        .nl("Ontvangen {} {}")
                        .slf4jArgs(impl, FileSizeFormatter.DEFAULT.format(fci.getCount())));
                } else {
                    logger.warn(en("Upload error ({}): {}")
                        .nl("Upload fout ({}) : {}")
                        .slf4jArgs(impl, fci.getException().get().getMessage())
                    );
                }
            }
        };
    }
}
