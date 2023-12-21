package nl.vpro.domain.media.update.collections;

import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.media.update.LocationUpdate;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class XmlCollectionTest {

    @Test
    public void marshal() {
        XmlCollection<LocationUpdate> col = new XmlCollection<>(Arrays.asList(new LocationUpdate()));
        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <collection xmlns:update="urn:vpro:media:update:2009" xmlns:media="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <update:location/>
            </collection>""";
        StringWriter writer = new StringWriter();
        JAXB.marshal(col, System.out);
        JAXB.marshal(col, writer);
        Diff diff = DiffBuilder.compare(expected).withTest(writer.toString()).build();
        assertFalse(diff.hasDifferences(), diff.toString() + " " + expected);
        XmlCollection<LocationUpdate> rounded = assertThatXml(col).isSimilarTo(expected).get();
        assertThat(rounded).isEqualTo(col);

    }

    @Test
    // TODO
    public void json() {
        XmlCollection<LocationUpdate> col = new XmlCollection<>(Arrays.asList(new LocationUpdate()));

        Jackson2TestUtil.roundTripAndSimilar(col, "{}");

    }

}
