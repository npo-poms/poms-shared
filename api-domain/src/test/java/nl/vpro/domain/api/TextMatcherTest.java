/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class TextMatcherTest {

    @Test
    public void testGetValue() {
        TextMatcher in = new TextMatcher("title");
        TextMatcher out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:textMatcher xmlns=\"urn:vpro:api:2013\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:media:2009\">title</local:textMatcher>");
        assertThat(out.getValue()).isEqualTo("title");
    }

    @Test
    public void testApplyText() {
        TextMatcher in = new TextMatcher("aaa");
        assertThat(in.test("aaa")).isTrue();
        assertThat(in.test("AAA")).isFalse();
        assertThat(in.test("aaaa")).isFalse();
        assertThat(in.test("xxx")).isFalse();
    }

    @Test
    public void testApplyRegexp() {
        TextMatcher in = new TextMatcher("(?i)a.a.*", StandardMatchType.REGEX);
        assertThat(in.test("aaa")).isTrue();
        assertThat(in.test("AAA")).isTrue();
        assertThat(in.test("aba")).isTrue();
        assertThat(in.test("xxx")).isFalse();
    }

    @Test
    public void testApplyWildcard() {
        TextMatcher in = new TextMatcher("aa*bb", StandardMatchType.WILDCARD);
        assertThat(in.test("aaxxxbb")).isTrue();
        assertThat(in.test("AAxxxBB")).isFalse();
        assertThat(in.test("aba")).isFalse();
        assertThat(in.test("aaxxxx")).isFalse();
    }

    @Test
    public void testApplyNot() {
        TextMatcher in = new TextMatcher("BB", Match.NOT);
        assertThat(in.test("BB")).isFalse();
        assertThat(in.test("AA")).isTrue();
    }
}
