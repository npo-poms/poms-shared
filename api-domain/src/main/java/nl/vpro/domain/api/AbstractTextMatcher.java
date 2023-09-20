/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.annotations.Beta;

import nl.vpro.domain.ToString;

/**
 * A text matcher matches strings (rather then e.g. dates or numbers)
 *
 * @author rico
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractTextMatcher<MT extends MatchType> extends AbstractMatcher<String> {
    public static final Match DEFAULT_MATCH = Match.MUST;

    @XmlValue
    protected String value;

    public AbstractTextMatcher(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public abstract MT getMatchType();

    public abstract void setMatchType(MT matchType);

    public String getFuzziness() {
        return null;
    }
    public void setFuzziness(String fuzziness) {

    }

    @Beta
    public abstract boolean isSemantic();

    public void setSemantic(Boolean semantic) {
        if (semantic != null && semantic) {
            throw new UnsupportedOperationException("Semantic search not suported by this text matcher");
        }
    }


    @Override
    public boolean test(@Nullable String input) {
        boolean result = getMatchType().eval(getValue(), input, isCaseSensitive());
        return match == Match.NOT ? ! result : result;
    }

    @Override
    public String toString() {
        ToStringBuilder builder =  ToString.builder(this)
            .append("value", value);
        if (match != null) {
            builder.append("match", match);
        }
        if (getMatchType() != null){
            builder.append("matchType", getMatchType());
        }
        if (getFuzziness() != null){
            builder.append("fuzziness", getFuzziness());
        }
        if (! isCaseSensitive()){
            builder.append("case sensitive", isCaseSensitive());
        }
        if (isSemantic()){
            builder.append("semantic", isSemantic());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof AbstractTextMatcher)) {
            return false;
        }

        AbstractTextMatcher<?> matcher = (AbstractTextMatcher)o;

        return value == null ? matcher.getValue() == null : value.equals(matcher.getValue()) && getMatch() == matcher.getMatch();

    }

    public abstract AbstractTextMatcher<MT> toLowerCase();

    protected String lowerCaseValue() {
        return value == null ? null : value.toLowerCase();
    }

    @Override
    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }


    public boolean isCaseSensitive() {
        return true;
    }

    public void setCaseSensitive(Boolean ignoreCase) {
        throw new UnsupportedOperationException();
    }


}
