/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.RangeMatcher;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationRangeMatcherType", propOrder = {"begin", "end"})
public class DurationRangeMatcher extends RangeMatcher<Duration> implements Predicate<Duration> {

    @XmlElement
    @Getter
    @Setter
    private Duration begin;
    @XmlElement
    @Getter
    @Setter
    private Duration end;


    public DurationRangeMatcher() {
    }

    public DurationRangeMatcher(Duration begin, Duration end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    public DurationRangeMatcher(Duration begin, Duration end) {
        super(begin, end, null);
    }


    @Override
    protected boolean defaultIncludeEnd() {
        return false;

    }

    @Override
    public boolean test(Duration duration) {
        return super.testComparable(duration);

    }
}
