/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import javax.xml.bind.JAXB;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class DateRangeFacetsTest {

    @Test
    public void testGetBeginWithPreset() throws Exception {
        DateRangeFacets<AbstractSearch> in = new DateRangeFacets<>();
        in.setRanges(Arrays.asList(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK));
        DateRangeFacets<AbstractSearch> out = JAXBTestUtil.roundTripAndSimilar(in,
                "<local:dateRangeFacets xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:preset>LAST_YEAR</api:preset>\n" +
                "    <api:preset>LAST_WEEK</api:preset>\n" +
                "</local:dateRangeFacets>");
        assertThat(out.getRanges()).containsOnly(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK);
        JAXB.marshal(in, System.out);
    }

    @Test
    public void testMixedJsonArrayPreset() throws Exception {
        Reader reader = new StringReader("[\"LAST_YEAR\",\"LAST_WEEK\",{\"name\":\"My range\",\"begin\":100,\"end\":200}]");
        DateRangeFacets<AbstractSearch> facet = (DateRangeFacets<AbstractSearch>) Jackson2Mapper.INSTANCE.readValue(reader, DateRangeFacets.class);
        assertThat(facet.getRanges()).containsOnly(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK, new DateRangeFacetItem("My range", new Date(100), new Date(200)));
    }

    @Test
    public void testJsonOutWithPreset() throws Exception {
        DateRangeFacets<AbstractSearch> in = new DateRangeFacets<>();
        in.setRanges(Arrays.asList(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK, new DateRangeFacetItem("My range", new Date(100), new Date(200))));
        Writer writer = new StringWriter();
        Jackson2Mapper.INSTANCE.writeValue(writer, in);
        assertThat(writer.toString()).isEqualTo("[\"LAST_YEAR\",\"LAST_WEEK\",{\"name\":\"My range\",\"begin\":100,\"end\":200}]");
    }
}
