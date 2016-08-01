/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.Group;

public class IsAnyGroup extends ArgumentMatcher<Group> {

    @Override
    public boolean matches(Object o) {
        return o == null || o instanceof Group;
    }
}