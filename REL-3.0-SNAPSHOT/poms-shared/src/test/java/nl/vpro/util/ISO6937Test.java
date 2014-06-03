package nl.vpro.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Michiel Meeuwissen
 */
public class ISO6937Test {

    @Test
    public void registration() {
        Charset charset = Charset.availableCharsets().get("ISO-6937");
        assertNotNull(charset);
        assertEquals(ISO6937CharsetProvider.ISO6937, charset);
    }

    @Test
    public void conversion()  {
        assertEquals(
            "\u00fc",
            new String(
                new byte[] { (byte) 0xc8, (byte) 'u'},
                ISO6937CharsetProvider.ISO6937));
        assertEquals(
            "Atat\u00fcrk",
            new String(
                new byte[] { 'A', 't', 'a', 't', (byte) 0xc8, 'u', 'r', 'k'},
                ISO6937CharsetProvider.ISO6937));


    }
    @Test
    public void longStrings() throws IOException {
        // Testing whether everything works ok on the buffer boundaries.

        // build a giant string with u-umlauts
        // and also a byte array in iso-6937 representing the exact same string
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StringBuilder      string = new StringBuilder();
        string.append("b");
        out.write('b');
        Random random = new Random(0);
        for (int i = 1 ; i < 10000; i++) {
            string.append("\u00fc");
            out.write(0xc8);
            out.write('u');
            if (i % (random.nextInt(25) + 1) == 0) {
                // some newlines here and there
                string.append('\n');
                out.write('\n');
            }
        }

        // now lets test whether the byte array actually is the expected string
        assertEquals(
            string.toString(),
            new String(out.toByteArray(), "ISO-6937"));
    }
}
