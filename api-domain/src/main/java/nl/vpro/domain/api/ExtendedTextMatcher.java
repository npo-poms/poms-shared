/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.ExtendedTextMatcherJson;
import nl.vpro.domain.api.validation.ValidTextMatcher;

/**
 * @author rico
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "extendedMatcherType")
@JsonSerialize(using = ExtendedTextMatcherJson.Serializer.class)
@JsonDeserialize(using = ExtendedTextMatcherJson.Deserializer.class)
@ValidTextMatcher
public class ExtendedTextMatcher extends AbstractTextMatcher<StandardMatchType> {
    @XmlAttribute
    @Pattern(regexp = "^AUTO|$")
    @Getter
    @Setter
    protected String fuzziness;

    public static final StandardMatchType DEFAULT_MATCHTYPE = StandardMatchType.TEXT;

    public static ExtendedTextMatcher must(String value) {
        return must(value, DEFAULT_MATCHTYPE);
    }


    public static ExtendedTextMatcher must(String value, boolean caseSensitive) {
        return must(value, DEFAULT_MATCHTYPE, caseSensitive);
    }

    public static ExtendedTextMatcher should(String value) {
        return should(value, DEFAULT_MATCHTYPE);
    }

    public static ExtendedTextMatcher not(String value) {
        return not(value, DEFAULT_MATCHTYPE);
    }


    public static ExtendedTextMatcher must(String value, StandardMatchType type) {
        return must(value, type, true);
    }

    public static ExtendedTextMatcher must(String value, StandardMatchType type, boolean caseSensitive) {
        return value == null ? null : new ExtendedTextMatcher(value, Match.MUST, type, caseSensitive);
    }


    public static ExtendedTextMatcher should(String value, StandardMatchType type) {
        return should(value, type, true);
    }

    public static ExtendedTextMatcher should(String value, StandardMatchType type, boolean caseSensitive) {
        return value == null ? null : new ExtendedTextMatcher(value, Match.SHOULD, type, caseSensitive);
    }

    public static ExtendedTextMatcher not(String value, StandardMatchType type) {
        return not(value, type, true);
    }

    public static ExtendedTextMatcher not(String value, StandardMatchType type, boolean caseSensitive) {
        return value == null ? null : new ExtendedTextMatcher(value, Match.NOT, type, caseSensitive);
    }


    @XmlAttribute
    private StandardMatchType matchType;

    @XmlAttribute
    private Boolean caseSensitive;

    public ExtendedTextMatcher( ) {
        this(null);
    }

    public ExtendedTextMatcher(String value) {
        this(value, (Match) null);
    }

    public ExtendedTextMatcher(String value, Match match) {
        this(value, match, null, true);
    }

    public ExtendedTextMatcher(String value, Match match, StandardMatchType matchType, boolean caseSensitive) {
        super(value);
        this.match = match == DEFAULT_MATCH ? null : match;
        this.matchType = matchType == StandardMatchType.TEXT ? null : matchType;
        this.caseSensitive = caseSensitive ? null : Boolean.FALSE;
    }

    public ExtendedTextMatcher(String value, StandardMatchType matchType) {
        this(value, matchType, true);
    }

    public ExtendedTextMatcher(String value, StandardMatchType matchType, boolean caseSensitive) {
        this(value, null, matchType, caseSensitive);
    }

    public ExtendedTextMatcher(String value, boolean caseSensitive) {
        this(value, null, null, caseSensitive);
    }

    @Override
    public StandardMatchType getMatchType() {
        return matchType == null ? DEFAULT_MATCHTYPE : matchType;
    }

    @Override
    public void setMatchType(StandardMatchType matchType) {
        this.matchType = matchType;
    }

    @Override
    public boolean isSemantic() {
        return false;
    }

    @Override
    public ExtendedTextMatcher toLowerCase() {
        return new ExtendedTextMatcher(lowerCaseValue(), match, matchType, caseSensitive);

    }

    @Override
    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive == null || caseSensitive ? null : caseSensitive;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive == null ? true : caseSensitive;
    }
}
