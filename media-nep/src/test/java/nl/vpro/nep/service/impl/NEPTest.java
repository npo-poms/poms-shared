package nl.vpro.nep.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Michiel Meeuwissen
 */
public class NEPTest {

    public static final Properties PROPERTIES = new Properties();

    static {
        try {
            PROPERTIES.load(new FileInputStream(
                new File(System.getProperty("user.home"), "conf" + File.separator + "nep.properties")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
