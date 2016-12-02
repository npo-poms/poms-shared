package nl.vpro.pages.domain.es;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public class ApiPagesIndex {

    public static String NAME = "apipages";

    public static String source() {
        return source("setting/apipages.json");
    }

    public static String source(String s) {
        try {
            StringWriter writer = new StringWriter();
            InputStream inputStream = ApiPagesIndex.class.getClassLoader().getResourceAsStream(s);
            if (inputStream == null) {
                throw new IllegalStateException("Could not find " + s);
            }
            IOUtils.copy(inputStream, writer, "utf-8");
            return writer.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
