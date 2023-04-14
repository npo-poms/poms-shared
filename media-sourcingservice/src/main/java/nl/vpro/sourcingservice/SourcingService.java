package nl.vpro.sourcingservice;

import java.io.InputStream;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;

public interface SourcingService {

    UploadResponse upload(SimpleLogger logger, String mid, long fileSize, InputStream inputStream);


    /**
     * A string which can be used to show where this implementation will upload to.
     */
    String getUploadString();

}
