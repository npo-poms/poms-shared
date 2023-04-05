package nl.vpro.sourcingservice;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Log4j2
class SourcingServiceImplTest {

    public static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(
                new File(System.getProperty("user.home"), "conf" + File.separator + "sourcingservice.properties")));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    SourcingServiceImpl impl;
    {
        /*ApiClient apiClient = new ApiClient();
        impl =  new SourcingServiceImpl(apiClient);
        apiClient.setBasePath("https://test.sourcing-audio.cdn.npoaudio.nl/");
        ((HttpBearerAuth) apiClient.getAuthentication("bearerAuth")).setBearerToken(PROPERTIES.getProperty("token"));
*/
        impl = new SourcingServiceImpl("https://test.sourcing-audio.cdn.npoaudio.nl/", PROPERTIES.getProperty("token"));
    }

    @Test
    @Disabled("This does actual stuff, need actual token. Ad wiremock version to test our part isolated.")
    public void upload() throws IOException {
        Path file = Paths.get(System.getProperty("user.home") , "samples", "sample.mp3");

        impl.upload("WO_KN_20053440", Files.size(file), Files.newInputStream(file));
    }


}
