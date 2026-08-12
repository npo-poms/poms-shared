package nl.vpro.nep.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class AbstractNEPTest {

    private final String prefix;


    Properties properties = new Properties();
    {
        File propFile = new File(new File(new File(System.getProperty("user.home")), "conf"), "nep.properties");
        try (FileInputStream input = new FileInputStream(propFile)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public AbstractNEPTest(String prefix) {
        this.prefix = prefix;
    }

    String getProperty(String name) {
        return properties.getProperty(prefix + "." + name);
    }
}
