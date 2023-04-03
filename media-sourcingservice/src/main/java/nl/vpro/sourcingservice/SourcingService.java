package nl.vpro.sourcingservice;

import java.io.InputStream;

public interface SourcingService {

    void upload(String mid, Long fileSize, InputStream inputStream);

}
