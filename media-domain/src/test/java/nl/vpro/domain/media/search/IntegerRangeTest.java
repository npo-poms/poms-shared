package nl.vpro.domain.media.search;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
public class IntegerRangeTest {

    @Test
    public void xml() {
        IntegerRange range = IntegerRange
            .builder()
            .start(IntegerRange.Value.of(1L))
            .stop(2L)
            .build();
        JAXBTestUtil.roundTripAndSimilarAndEquals(range,
                "<local:integerRange xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:local=\"uri:local\">\n" +
                "    <s:start>1</s:start>\n" +
                "    <s:stop inclusive=\"false\">2</s:stop>\n" +
                "</local:integerRange>");
    }

}
