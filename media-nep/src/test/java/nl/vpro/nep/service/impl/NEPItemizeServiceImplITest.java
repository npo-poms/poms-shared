package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Properties;

import org.junit.Test;

import nl.vpro.nep.domain.NEPItemizeRequest;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class NEPItemizeServiceImplITest {

    @Test
    public void itemize() throws IOException {
        Instant start = Instant.now();
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File(System.getProperty("user.home"), "conf" + File.separator + "nep.properties")));
        NEPItemizeServiceImpl itemizer = new NEPItemizeServiceImpl(properties);
        NEPItemizeRequest request = new NEPItemizeRequest();
        request.setIdentifier("AT_2073522");
        request.setStarttime("00:00:00.000");
        request.setEndtime("00:02:21.151");
        log.info("response: {} {}", itemizer.itemize(request), start);


    }
}
