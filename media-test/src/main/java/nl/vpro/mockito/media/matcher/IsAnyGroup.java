/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.Group;

public class IsAnyGroup implements ArgumentMatcher<Group> {

    @Override
    public boolean matches(Group o) {
        return true;
    }
}
