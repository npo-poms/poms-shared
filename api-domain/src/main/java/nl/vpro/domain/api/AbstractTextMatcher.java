/*
 * Copyright (C) 2016 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;

/**
 * @author rico
 * @since 4.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
public abstract class AbstractTextMatcher<MT extends MatchType> extends AbstractMatcher implements Matcher, Predicate<String> {
    public static final Match DEFAULT_MATCH = Match.MUST;

    @XmlValue
    protected String value;

    @XmlAttribute
    @Pattern(regexp = "^1|2|3|AUTO|0\\.[1-9]$")
    @Getter
    @Setter
    protected String fuzziness;

    public AbstractTextMatcher(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public abstract MT getMatchType();

    public abstract void setMatchType(MT matchType);

    @Override
    public boolean test(@Nullable String input) {
        boolean result = getMatchType().eval(getValue(), input, isCaseSensitive());
        return match == Match.NOT ? ! result : result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        sb.append("value='").append(getValue()).append('\'');
        if(match != null) {
            sb.append(", match='").append(match).append('\'');
        }
        if(getMatchType() != null) {
            sb.append(", matchType='").append(getMatchType().getName()).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof AbstractTextMatcher)) {
            return false;
        }

        AbstractTextMatcher matcher = (AbstractTextMatcher)o;

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
