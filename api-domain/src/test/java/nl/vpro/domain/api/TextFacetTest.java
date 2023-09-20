/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**

 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class TextFacetTest  {
    @Test
    public void testGetThreshold() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setThreshold(1111);
        TextFacet<?, ?> out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:textFacet sort="VALUE_ASC" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                    <api:threshold>1111</api:threshold>
                    <api:max>24</api:max>
                </local:textFacet>""");
        assertThat(out.getThreshold()).isEqualTo(1111);
    }

    @Test
    public void testGetThreshold0Json() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setThreshold(0);
        TextFacet<?, ?> out = Jackson2TestUtil.roundTripAndSimilar(in,
            """
                {
                  "threshold" : 0,
                  "sort" : "VALUE_ASC",
                  "max" : 24
                }""");
        assertThat(out.getThreshold()).isEqualTo(0);
    }

    @Test
    public void testGetSort() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setSort(FacetOrder.VALUE_DESC);
        TextFacet<?, String> out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:textFacet sort="VALUE_DESC" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                    <api:max>24</api:max>
                </local:textFacet>""");
        assertThat(out.getSort()).isEqualTo(FacetOrder.VALUE_DESC);
    }

    @Test
    public void testGetIncludeXml() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setInclude("3\\.0\\.1\\.[0-9]+");
        JAXBTestUtil.roundTripAndSimilar(in, """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <local:textFacet sort="VALUE_ASC" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                <api:max>24</api:max>
                <api:include>3\\.0\\.1\\.[0-9]+</api:include>
            </local:textFacet>""");
    }
}
