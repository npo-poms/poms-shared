/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.Segment;

public class IsAnySegment implements ArgumentMatcher<Segment> {

    @Override
    public boolean matches(Segment o) {
        return true;
    }
}
