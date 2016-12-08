/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.matcher;

import org.mockito.ArgumentMatcher;

import nl.vpro.domain.media.Program;

public class IsAnyProgram implements  ArgumentMatcher<Program> {
    @Override
    public boolean matches(Program o) {
        return true;
    }

}
