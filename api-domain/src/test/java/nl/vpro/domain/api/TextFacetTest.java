/*
 * Copyright (C) 2013 All rights reserved
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
            "<local:textFacet sort=\"VALUE_ASC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:threshold>1111</api:threshold>\n" +
                "    <api:max>24</api:max>\n" +
                "</local:textFacet>");
        assertThat(out.getThreshold()).isEqualTo(1111);
    }

    @Test
    public void testGetThreshold0Json() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setThreshold(0);
        TextFacet<?, ?> out = Jackson2TestUtil.roundTripAndSimilar(in,
            "{\n" +
                "  \"threshold\" : 0,\n" +
                "  \"sort\" : \"VALUE_ASC\",\n" +
                "  \"max\" : 24\n" +
                "}");
        assertThat(out.getThreshold()).isEqualTo(0);
    }

    @Test
    public void testGetSort() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setSort(FacetOrder.VALUE_DESC);
        TextFacet<?, String> out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:textFacet sort=\"VALUE_DESC\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:max>24</api:max>\n" +
                "</local:textFacet>");
        assertThat(out.getSort()).isEqualTo(FacetOrder.VALUE_DESC);
    }

    @Test
    public void testGetIncludeXml() {
        TextFacet<?, String> in = new TextFacet<TermSearch, String>();
        in.setInclude("3\\.0\\.1\\.[0-9]+");
        JAXBTestUtil.roundTripAndSimilar(in, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<local:textFacet sort=\"VALUE_ASC\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
            "    <api:max>24</api:max>\n" +
            "    <api:include>3\\.0\\.1\\.[0-9]+</api:include>\n" +
            "</local:textFacet>");
    }
}
