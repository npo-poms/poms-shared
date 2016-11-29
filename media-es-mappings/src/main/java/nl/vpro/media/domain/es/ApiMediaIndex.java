package nl.vpro.media.domain.es;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class ApiMediaIndex {

    public static String NAME = "apimedia";

    public static String source() {
        return source("setting/apimedia.json");
    }

    public static String source(String name) {
        try {
            StringWriter writer = new StringWriter();
            InputStream inputStream = ApiMediaIndex.class.getClassLoader().getResourceAsStream(name);
            if (inputStream == null) {
                throw new IllegalStateException("Could not find " + name);
            }
            IOUtils.copy(inputStream, writer, "utf-8");
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
