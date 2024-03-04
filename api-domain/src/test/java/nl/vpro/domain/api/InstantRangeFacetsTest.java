/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.Arrays;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class InstantRangeFacetsTest {

    @Test
    public void testGetBeginWithPreset() {
        DateRangeFacets<AbstractSearch<?>> in = new DateRangeFacets<>();
        in.setRanges(Arrays.asList(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK));
        DateRangeFacets<AbstractSearch<?>> out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:dateRangeFacets xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                    <api:preset>LAST_YEAR</api:preset>
                    <api:preset>LAST_WEEK</api:preset>
                </local:dateRangeFacets>""");
        assertThat(out.getRanges()).containsOnly(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK);
        JAXB.marshal(in, System.out);
    }

    @Test
    public void testMixedJsonArrayPreset() throws Exception {
        Reader reader = new StringReader("[\"LAST_YEAR\",\"LAST_WEEK\",{\"name\":\"My range\",\"begin\":100,\"end\":200}]");
        DateRangeFacets<AbstractSearch<?>> facet = (DateRangeFacets<AbstractSearch<?>>) Jackson2Mapper.getInstance().readValue(reader, DateRangeFacets.class);
        assertThat(facet.getRanges()).containsOnly(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK, new DateRangeFacetItem("My range", Instant.ofEpochMilli(100), Instant.ofEpochMilli(200)));
    }

    @Test
    public void testJsonOutWithPreset() throws Exception {
        DateRangeFacets<AbstractSearch<?>> in = new DateRangeFacets<>();
        in.setRanges(Arrays.asList(DateRangePreset.LAST_YEAR, DateRangePreset.LAST_WEEK, new DateRangeFacetItem("My range", Instant.ofEpochMilli(100), Instant.ofEpochMilli(200))));
        Writer writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, in);
        assertThat(writer.toString()).isEqualTo("[\"LAST_YEAR\",\"LAST_WEEK\",{\"name\":\"My range\",\"begin\":100,\"end\":200}]");
    }
}
