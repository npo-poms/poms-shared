/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import java.io.Reader;
import java.io.StringReader;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.DateRangeFacetItem;
import nl.vpro.domain.api.DateRangePreset;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class PageFacetsTest {

    @Test
    public void testDateRangeWithPresetWhenNoArray() throws Exception {
        Reader reader = new StringReader("{\"sortDates\" : \"LAST_YEAR\"}");
        PageFacets facets = Jackson2Mapper.INSTANCE.readValue(reader, PageFacets.class);
        assertThat(facets.getSortDates().getRanges()).containsOnly(DateRangePreset.LAST_YEAR);
    }

    @Test
    public void testDateRangeCustomWhenNoArray() throws Exception {
        Reader reader = new StringReader("{\"sortDates\" : {\"name\":\"My range\",\"begin\":100,\"end\":200}}");
        PageFacets facets = Jackson2Mapper.INSTANCE.readValue(reader, PageFacets.class);
        assertThat(facets.getSortDates().getRanges()).containsOnly(new DateRangeFacetItem("My range", Instant.ofEpochMilli(100), Instant.ofEpochMilli(200)));
    }


    @Test
    public void testSectionFacets() throws Exception {
        Reader reader = new StringReader("{\"sections\" : {}}");
        PageFacets facets = Jackson2Mapper.INSTANCE.readValue(reader, PageFacets.class);
        System.out.println(facets.getSections());
    }
}
