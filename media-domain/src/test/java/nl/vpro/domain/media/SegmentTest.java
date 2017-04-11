package nl.vpro.domain.media;

import java.time.Duration;

import org.junit.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class SegmentTest {


    @Test
    public void xml() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><segment midRef=\"RBX_NTR_2648108\" type=\"SEGMENT\" urnRef=\"urn:vpro:media:program:83538010\" avType=\"AUDIO\" embeddable=\"true\" mid=\"RBX_NTR_4965178\" sortDate=\"2013-11-01T04:36:35.076+01:00\" creationDate=\"2016-11-01T04:36:35.076+01:00\" lastModified=\"2016-11-01T04:36:35.105+01:00\" publishDate=\"2016-11-01T04:42:23.506+01:00\" urn=\"urn:vpro:media:segment:83538015\" workflow=\"PUBLISHED\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\"><crid>crid://item.radiobox2/372578</crid><broadcaster id=\"NTR\">NTR</broadcaster><title owner=\"RADIOBOX\" type=\"MAIN\">Monteverdi - Orfeo</title><description owner=\"RADIOBOX\" type=\"MAIN\">Opname 12 oktober 2016, Opera LausanneJose Maria Lo Monaco &amp; Fernando Guimaraes &amp; Anais Yvoz &amp; Delphine Galou &amp; Nicolas Courjal &amp; Anicio Zorzi Giustiniani &amp; Alessandro Giangrande &amp; Mathilde Opinel</description><duration>P0DT0H3M0.000S</duration><credits/><descendantOf urnRef=\"urn:vpro:media:group:13405607\" midRef=\"AUTO_NTROPERALIVE\" type=\"SERIES\"/><descendantOf urnRef=\"urn:vpro:media:program:83538010\" midRef=\"RBX_NTR_2648108\" type=\"BROADCAST\"/><locations/><scheduleEvents/><images><shared:image owner=\"RADIOBOX\" type=\"PICTURE\" highlighted=\"false\" creationDate=\"2016-11-01T04:36:34.659+01:00\" lastModified=\"2016-11-01T04:36:35.079+01:00\" urn=\"urn:vpro:media:image:83538017\" workflow=\"PUBLISHED\"><shared:title>Orfeo ed Euridice</shared:title><shared:imageUri>urn:vpro:image:780595</shared:imageUri><shared:height>568</shared:height><shared:width>480</shared:width></shared:image></images><availableSubtitles/><start>P0DT0H0M0.000S</start></segment>";
        Segment segment = JAXBTestUtil.unmarshal(xml,  Segment.class);
        JAXBTestUtil.roundTripAndSimilarAndEquals(segment, xml);
    }

    @Test
    public void copy() {
        Program program = MediaBuilder.program().build();
        Segment segment = MediaBuilder.segment().mainTitle("bla").duration(Duration.ofSeconds(123)).parent(program).build();

        Segment copy = Segment.copy(segment);
        assertThat(copy.getMainTitle()).isEqualTo("bla");
        assertThat(copy.getDuration().get()).isEqualTo(Duration.ofSeconds(123));

    }

}
