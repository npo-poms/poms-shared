/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.api.StandardMatchType.REGEX;
import static nl.vpro.domain.api.StandardMatchType.WILDCARD;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rico
 * @since 4.6
 */
public class ExtendedTextMatcherTest {
    @Test
    public void testGetValue() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("title");
        ExtendedTextMatcher out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:extendedTextMatcher xmlns=\"urn:vpro:api:2013\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:media:2009\">title</local:extendedTextMatcher>");
        assertThat(out.getValue()).isEqualTo("title");
    }

    @Test
    public void testApplyText() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("aaa");
        assertThat(in.test("aaa")).isTrue();
        assertThat(in.test("AAA")).isFalse();
        assertThat(in.test("aaaa")).isFalse();
        assertThat(in.test("xxx")).isFalse();
    }

    @Test
    public void testApplyRegexp() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("a.a.*", REGEX);
        assertThat(in.test("aaa")).isTrue();
        assertThat(in.test("AAA")).isFalse();
        assertThat(in.test("aba")).isTrue();
        assertThat(in.test("xxx")).isFalse();
    }

    @Test
    public void testApplyRegexpIgnoreCase() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("a.a.*", REGEX, false);
        assertThat(in.test("aaa")).isTrue();
        assertThat(in.test("AAA")).isTrue();
        assertThat(in.test("aba")).isTrue();
        assertThat(in.test("xxx")).isFalse();
    }

    @Test
    public void testApplyWildcard() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("aa*bb", WILDCARD);
        assertThat(in.test("aaxxxbb")).isTrue();
        assertThat(in.test("AAxxxBB")).isFalse();
        assertThat(in.test("aba")).isFalse();
        assertThat(in.test("aaxxxx")).isFalse();
    }


    @Test
    public void testApplyWildcardIgnoreCase() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("aa*bb", WILDCARD, false);
        assertThat(in.test("aaxxxbb")).isTrue();
        assertThat(in.test("AAxxxBB")).isTrue();
        assertThat(in.test("aba")).isFalse();
        assertThat(in.test("aaxxxx")).isFalse();
    }

    @Test
    public void testApplyNot() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("BB", Match.NOT);
        assertThat(in.test("BB")).isFalse();
        assertThat(in.test("AA")).isTrue();
    }

    @Test
    public void testApplyIgnoreCase() {
        ExtendedTextMatcher in = new ExtendedTextMatcher("aaa", Match.MUST, StandardMatchType.TEXT, false);
        assertThat(in.test("aaa")).isTrue();
        assertThat(in.test("AAA")).isTrue();
        assertThat(in.test("aaaa")).isFalse();
        assertThat(in.test("xxx")).isFalse();
    }
}
