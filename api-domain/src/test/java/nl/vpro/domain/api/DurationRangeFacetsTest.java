/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class DurationRangeFacetsTest {

    @Test
    public void testJsonArray() throws Exception {
        Reader reader = new StringReader("[{\"name\":\"My range\",\"begin\":100,\"end\":200}]");
        DurationRangeFacets<AbstractSearch> facet = (DurationRangeFacets<AbstractSearch>) Jackson2Mapper.INSTANCE.readValue(reader, DurationRangeFacets.class);
        assertThat(facet.getRanges()).containsOnly(new DurationRangeFacetItem("My range", Duration.ofMillis(100), Duration.ofMillis(200)));
    }

}
