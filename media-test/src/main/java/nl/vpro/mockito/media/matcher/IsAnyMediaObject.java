/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.MediaObject;

public class IsAnyMediaObject implements ArgumentMatcher<MediaObject>{
    @Override
    public boolean matches(MediaObject o) {
        return true;
    }
}
