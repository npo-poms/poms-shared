package nl.vpro.sourcingservice;

import java.io.*;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import nl.vpro.sourcingservice.invoker.ApiClient;
import nl.vpro.sourcingservice.invoker.ApiException;
import nl.vpro.sourcingservice.invoker.auth.HttpBearerAuth;

import static org.junit.jupiter.api.Assertions.*;

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
        ApiClient apiClient = new ApiClient();
        impl =  new SourcingServiceImpl(apiClient);
        apiClient.setBasePath("https://test.sourcing-audio.cdn.npoaudio.nl/");
        ((HttpBearerAuth) apiClient.getAuthentication("bearerAuth")).setBearerToken(PROPERTIES.getProperty("token"));

    }

    @Test
    public void upload() throws ApiException, FileNotFoundException {
        impl.upload("WO_KN_20053440", new FileInputStream(new File("test.mp3")));;
    }

}
