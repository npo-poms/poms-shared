/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.ExtendedTextMatcherListJson;

/**
 * @author rico
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "extendedTextMatcherListType")
@JsonSerialize(using = ExtendedTextMatcherListJson.Serializer.class)
@JsonDeserialize(using = ExtendedTextMatcherListJson.Deserializer.class)
public class ExtendedTextMatcherList extends AbstractTextMatcherList<ExtendedTextMatcher, StandardMatchType> {

    public ExtendedTextMatcherList() {
        super();
    }

    public static ExtendedTextMatcherList must(ExtendedTextMatcher... values) {
        return new ExtendedTextMatcherList(Match.MUST, values);
    }

    public static ExtendedTextMatcherList must(Stream<ExtendedTextMatcher> values) {
        return new ExtendedTextMatcherList(values.collect(Collectors.toList()), Match.MUST);
    }

    public static ExtendedTextMatcherList should(ExtendedTextMatcher... values) {
        return new ExtendedTextMatcherList(Match.SHOULD, values);
    }

    public static ExtendedTextMatcherList should(Stream<ExtendedTextMatcher> values) {
        return new ExtendedTextMatcherList(values.collect(Collectors.toList()), Match.SHOULD);
    }

    public static ExtendedTextMatcherList not(ExtendedTextMatcher... values) {
        return new ExtendedTextMatcherList(Match.NOT, values);
    }

    public static ExtendedTextMatcherList not(Stream<ExtendedTextMatcher> values) {
        return new ExtendedTextMatcherList(values.collect(Collectors.toList()), Match.NOT);
    }

    public ExtendedTextMatcherList(List<ExtendedTextMatcher> values, Match match) {
        super(match, values);
    }

    public ExtendedTextMatcherList(ExtendedTextMatcher... values) {
        super(DEFAULT_MATCH, Arrays.asList(values));
    }

    public ExtendedTextMatcherList(Match match, ExtendedTextMatcher... values) {
        super(match, Arrays.asList(values));
    }

    @Override
    @XmlElement(name = "matcher")
    public List<ExtendedTextMatcher> getMatchers() {
        return super.getMatchers();
    }

    @Override
    public void setMatchers(List<ExtendedTextMatcher> matchers) {
        super.setMatchers(matchers);
    }



}
