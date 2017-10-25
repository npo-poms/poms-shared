/*
 * Copyright (C) 2016 All rights reserved
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
public class ExtendedTextMatcher extends AbstractTextMatcher<ExtendedMatchType> {
    @XmlAttribute
    @Pattern(regexp = "^AUTO|$")
    @Getter
    @Setter
    protected String fuzziness;

    public static final ExtendedMatchType DEFAULT_MATCHTYPE = ExtendedMatchType.TEXT;

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


    public static ExtendedTextMatcher must(String value, ExtendedMatchType type) {
        return must(value, type, true);
    }

    public static ExtendedTextMatcher must(String value, ExtendedMatchType type, boolean caseSensitive) {
        return value == null ? null : new ExtendedTextMatcher(value, Match.MUST, type, caseSensitive);
    }


    public static ExtendedTextMatcher should(String value, ExtendedMatchType type) {
        return should(value, type, true);
    }

    public static ExtendedTextMatcher should(String value, ExtendedMatchType type, boolean caseSensitive) {
        return value == null ? null : new ExtendedTextMatcher(value, Match.SHOULD, type, caseSensitive);
    }

    public static ExtendedTextMatcher not(String value, ExtendedMatchType type) {
        return not(value, type, true);
    }

    public static ExtendedTextMatcher not(String value, ExtendedMatchType type, boolean caseSensitive) {
        return value == null ? null : new ExtendedTextMatcher(value, Match.NOT, type, caseSensitive);
    }


    @XmlAttribute
    private ExtendedMatchType matchType;

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

    public ExtendedTextMatcher(String value, Match match, ExtendedMatchType matchType, boolean caseSensitive) {
        super(value);
        this.match = match == DEFAULT_MATCH ? null : match;
        this.matchType = matchType == ExtendedMatchType.TEXT ? null : matchType;
        this.caseSensitive = caseSensitive ? null : Boolean.FALSE;
    }

    public ExtendedTextMatcher(String value, ExtendedMatchType matchType) {
        this(value, matchType, true);
    }


    public ExtendedTextMatcher(String value, ExtendedMatchType matchType, boolean caseSensitive) {
        this(value, null, matchType, caseSensitive);
    }

    public ExtendedTextMatcher(String value, boolean caseSensitive) {
        this(value, null, null, caseSensitive);
    }

    @Override
    public ExtendedMatchType getMatchType() {
        return matchType == null ? DEFAULT_MATCHTYPE : matchType;
    }

    @Override
    public void setMatchType(ExtendedMatchType matchType) {
        this.matchType = matchType;
    }

    @Override
    public ExtendedTextMatcher toLowerCase() {
        return new ExtendedTextMatcher(lowerCaseValue(), match, matchType, caseSensitive);

    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive ? null : caseSensitive;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive == null ? true : caseSensitive;
    }
}
