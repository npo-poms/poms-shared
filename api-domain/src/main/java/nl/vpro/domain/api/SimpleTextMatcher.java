/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;

import org.meeuw.xml.bind.annotation.XmlDocumentation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;

import nl.vpro.domain.api.jackson.SimpleTextMatcherJson;
import nl.vpro.domain.api.validation.ValidTextMatcher;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "simpleMatcherType")
@JsonSerialize(using = SimpleTextMatcherJson.Serializer.class)
@JsonDeserialize(using = SimpleTextMatcherJson.Deserializer.class)
@ValidTextMatcher
public class SimpleTextMatcher extends AbstractTextMatcher<SimpleMatchType> {

    @XmlAttribute
    @Pattern(regexp = "^AUTO|$")
    @Getter
    @Setter
    protected String fuzziness;

    @XmlAttribute
    @XmlDocumentation("Whether the search must happen via the semantic vectorization. This is beta feature, which may not be enabled.")
    private Boolean semantic;


    public static final SimpleMatchType DEFAULT_MATCHTYPE = SimpleMatchType.TEXT;

    public static SimpleTextMatcher must(String value) {
        return must(value, DEFAULT_MATCHTYPE);
    }

    public static SimpleTextMatcher should(String value) {
        return should(value, DEFAULT_MATCHTYPE);
    }

    public static SimpleTextMatcher not(String value) {
        return not(value, DEFAULT_MATCHTYPE);
    }


    public static SimpleTextMatcher must(String value, SimpleMatchType type) {
        return value == null ? null : new SimpleTextMatcher(value, Match.MUST, type);
    }

    public static SimpleTextMatcher should(String value, SimpleMatchType type) {
        return value == null ? null : new SimpleTextMatcher(value, Match.SHOULD, type);
    }

    public static SimpleTextMatcher not(String value, SimpleMatchType type) {
        return value == null ? null : new SimpleTextMatcher(value, Match.NOT, type);
    }


    @XmlAttribute
    private SimpleMatchType matchType;

    public SimpleTextMatcher( ) {
        this(null);
    }

    public SimpleTextMatcher(String value) {
        this(value, (Match) null);
    }

    public SimpleTextMatcher(String value, Match match) {
        this(value, match, null);
    }

    public SimpleTextMatcher(String value, Match match, SimpleMatchType matchType) {
        this(value, match, matchType,null);
    }

    @lombok.Builder
    private SimpleTextMatcher(String value, Match match, SimpleMatchType matchType, Boolean semantic) {
        super(value);
        this.match = match == DEFAULT_MATCH ? null : match;
        this.matchType = matchType == SimpleMatchType.TEXT ? null : matchType;
        this.semantic = semantic != null && semantic ? true : null;
    }

    public SimpleTextMatcher(String value, SimpleMatchType matchType) {
        this(value, null, matchType);
    }

    @Override
    public SimpleMatchType getMatchType() {
        return matchType == null ? DEFAULT_MATCHTYPE : matchType;
    }

    @Override
    public void setMatchType(SimpleMatchType matchType) {
        this.matchType = matchType;
    }

    @Override
    @Beta
    public boolean isSemantic() {
        return semantic != null && semantic;
    }

    @Override
    public  void setSemantic(Boolean b) {
        this.semantic = b != null && b ? true : null;
    }


    @Override
    public SimpleTextMatcher toLowerCase() {
        return new SimpleTextMatcher(lowerCaseValue(), match, matchType);
    }

}
