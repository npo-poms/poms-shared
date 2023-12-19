/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.TextMatcherJson;
import nl.vpro.domain.api.validation.ValidTextMatcher;

/**
 * Contains a text to be matched against one or more fields or properties. The textual value originates from user input
 * or a profile configuration. End-users mostly submit a single text field but when input might be applied to
 * several fields to query for this text.
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "textMatcherType")
@JsonSerialize(using = TextMatcherJson.Serializer.class)
@JsonDeserialize(using = TextMatcherJson.Deserializer.class)
@ValidTextMatcher
public class TextMatcher extends AbstractTextMatcher<StandardMatchType> {

    public static final StandardMatchType DEFAULT_MATCHTYPE = StandardMatchType.TEXT;

    public static TextMatcher must(String value) {
        return must(value, DEFAULT_MATCHTYPE);
    }
    public static TextMatcher should(String value) {
        return should(value, DEFAULT_MATCHTYPE);
    }

    public static TextMatcher not(String value) {
        return not(value, DEFAULT_MATCHTYPE);
    }


    public static TextMatcher must(String value, StandardMatchType type) {
        return value == null ? null : new TextMatcher(value, Match.MUST, type);
    }

    public static TextMatcher should(String value, StandardMatchType type) {
        return value == null ? null : new TextMatcher(value, Match.SHOULD, type);
    }

    public static TextMatcher not(String value, StandardMatchType type) {
        return value ==  null ? null : new TextMatcher(value, Match.NOT, type);
    }


    @XmlAttribute
    private StandardMatchType matchType;

    public TextMatcher() {
        this(null);
    }

    public TextMatcher(String value) {
        this(value, (Match) null);
    }

    public TextMatcher(String value, Match match) {
        this(value, match, null);
    }

    public TextMatcher(String value, Match match, StandardMatchType matchType) {
        super(value);
        this.match = match == DEFAULT_MATCH ? null : match;
        this.matchType = matchType == StandardMatchType.TEXT ? null : matchType;
    }
    public TextMatcher(String value, StandardMatchType matchType) {
        this(value, null, matchType);
    }

    @Override
    @XmlDocumentation("How to match. Defaults to TEXT")
    public StandardMatchType getMatchType() {
        return matchType == null ? DEFAULT_MATCHTYPE : matchType;
    }

    @Override
    public void setMatchType(StandardMatchType matchType) {
        this.matchType = matchType == null ? null : StandardMatchType.valueOf(matchType.getName());
    }

    @Override
    public boolean isSemantic() {
        return false;
    }

    @Override
    public TextMatcher toLowerCase() {
        return new TextMatcher(lowerCaseValue(), match, matchType);
    }

}
