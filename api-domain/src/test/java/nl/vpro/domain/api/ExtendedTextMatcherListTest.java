/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rico
 * @since 4.6
 */
public class ExtendedTextMatcherListTest {

    @Test
    public void marshal() {
        final ExtendedTextMatcherList textMatcherList = new ExtendedTextMatcherList(
                Arrays.asList(new ExtendedTextMatcher("a", Match.SHOULD, StandardMatchType.TEXT, false),
                        new ExtendedTextMatcher("b", Match.SHOULD)),
                Match.MUST);
        ExtendedTextMatcherList result = JAXBTestUtil.roundTripAndSimilar(textMatcherList,
            """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <local:extendedTextMatcherList match="MUST" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                    <api:matcher caseSensitive="false" match="SHOULD">a</api:matcher>
                    <api:matcher match="SHOULD">b</api:matcher>
                </local:extendedTextMatcherList>""");

        assertThat(result.asList().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(result.asList().get(0).getMatchType()).isEqualTo(StandardMatchType.TEXT);
        assertThat(result.asList().get(0).isCaseSensitive()).isEqualTo(Boolean.FALSE);
    }
}
