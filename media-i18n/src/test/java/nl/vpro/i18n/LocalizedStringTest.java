package nl.vpro.i18n;

import java.io.IOException;

import javax.xml.bind.annotation.XmlElement;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 0.50
 */
public class LocalizedStringTest {

    public static class A {
        @XmlElement
        LocalizedString string;
    }

    @Test
    public void xml() throws IOException, SAXException {
        A a = new A();
        a.string = LocalizedString.of("bla", Locales.DUTCH);
        JAXBTestUtil.roundTripAndSimilar(a, "<local:a xmlns:local=\"uri:local\">\n" +
            "    <string xml:lang=\"nl_NL\">bla</string>\n" +
            "</local:a>");
    }

}
