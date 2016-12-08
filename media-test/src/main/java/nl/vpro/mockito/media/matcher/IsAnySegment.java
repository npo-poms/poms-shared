/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Segment;

public class IsAnySegment implements ArgumentMatcher<MediaObject> {

    @Override
    public boolean matches(MediaObject o) {
        return o == null || o instanceof Segment;
    }
}
