/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@XmlTransient
public abstract class SimpleRangeMatcher<V extends Comparable<V>> extends RangeMatcher<V, V> {
    public SimpleRangeMatcher() {
    }

    protected SimpleRangeMatcher(V begin, V end) {
        super(begin, end);
    }

    protected SimpleRangeMatcher(V begin, V end, Boolean inclusiveEnd) {
        super(begin, end, inclusiveEnd);
    }

    protected SimpleRangeMatcher(V begin, V end, Boolean inclusiveEnd, Match match) {
        super(begin, end, inclusiveEnd, match);
    }

    @Override
    public boolean test(V t) {
        return valueTest(t);
    }

}
