/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
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
