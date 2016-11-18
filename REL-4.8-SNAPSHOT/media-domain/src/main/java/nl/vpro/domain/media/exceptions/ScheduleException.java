/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.exceptions;

public class ScheduleException extends RuntimeException {
    public ScheduleException() {
    }

    public ScheduleException(String s) {
        super(s);
    }

    public ScheduleException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ScheduleException(Throwable throwable) {
        super(throwable);
    }
}
