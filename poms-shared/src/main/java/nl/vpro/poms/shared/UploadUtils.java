package nl.vpro.poms.shared;

import java.util.function.Consumer;

import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileCachingInputStream;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.i18n.MultiLanguageString.en;

/**
 * @since 8.10 (Used to be in SourcingService)
 */
public class UploadUtils {


    /**
     * The current phase of the upload, used to log the phase in which the upload is.
     * This is also included in the id of the html message that is sent to the GUI.
     */
    public static final ThreadLocal<String> PHASE = ThreadLocal.withInitial(() -> null);

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
