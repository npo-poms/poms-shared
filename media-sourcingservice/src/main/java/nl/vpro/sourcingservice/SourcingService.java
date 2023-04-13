package nl.vpro.sourcingservice;

import java.io.InputStream;

import nl.vpro.logging.simple.SimpleLogger;

public interface SourcingService {

    void upload(SimpleLogger logger, String mid, long fileSize, InputStream inputStream);

}
