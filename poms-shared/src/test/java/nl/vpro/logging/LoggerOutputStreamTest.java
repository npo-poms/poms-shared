package nl.vpro.logging;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 */
public class LoggerOutputStreamTest {


    @Test
    public void test() throws IOException {
        String testString = "bla bla\n\nbloe bloe ";
        final StringBuffer buf = new StringBuffer();
        LoggerOutputStream instance = new LoggerOutputStream(null, false) {
            @Override
            void log(String line) {
                buf.append(line).append("\n");
            }
        };
        instance.write(testString.getBytes());
        instance.close();
        assertEquals(testString + "\n", buf.toString());
    }

}
