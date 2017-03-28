package nl.vpro.domain.media;

import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.support.TextualType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class ScheduleEventTest {

    @Test
    public void testTitles() throws IOException, SAXException {

        ScheduleEvent e = new ScheduleEvent();
        e.setTitle("bbb", TextualType.ABBREVIATION);
        e.setMainTitle("aa");

        assertThat(e.getTitles().first().get()).isEqualTo("aa");

        JAXBTestUtil.roundTripAndSimilar(e, "<local:scheduleEvent xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <title owner=\"BROADCASTER\" type=\"MAIN\">aa</title>\n" +
            "    <title owner=\"BROADCASTER\" type=\"ABBREVIATION\">bbb</title>\n" +
            "</local:scheduleEvent>");


    }
}
