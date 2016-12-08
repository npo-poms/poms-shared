/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.Schedule;

public class IsAnySchedule implements  ArgumentMatcher<Schedule> {

    @Override
    public boolean matches(Schedule o) {
        return true;
    }
}
