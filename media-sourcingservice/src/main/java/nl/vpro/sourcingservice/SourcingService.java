package nl.vpro.sourcingservice;

import java.io.InputStream;

public interface SourcingService {

    void upload(String mid, long fileSize, InputStream inputStream);

}
