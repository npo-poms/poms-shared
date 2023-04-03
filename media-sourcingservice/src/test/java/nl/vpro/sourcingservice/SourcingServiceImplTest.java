package nl.vpro.sourcingservice;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class SourcingServiceImplTest {

    public static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(
                new File(System.getProperty("user.home"), "conf" + File.separator + "sourcingservice.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
    public void upload() throws IOException {
        Path file = Paths.get(System.getProperty("user.home") , "samples", "sample.mp3");

        impl.upload("WO_KN_20053440", Files.size(file), Files.newInputStream(file));
    }


}
