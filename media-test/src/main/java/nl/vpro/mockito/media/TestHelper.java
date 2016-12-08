/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media;

import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.media.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

public class TestHelper {

    @Deprecated
    public static <T> Answer<T> firstArgument(Class<T> clazz) {
        return returnsFirstArg();
    }

    @Deprecated
    public static Answer<MediaObject> withSameMediaObject() {
        return returnsFirstArg();
    }

    @Deprecated
    public static Answer<Schedule> withSameSchedule() {
        return returnsFirstArg();
    }

    public static <T> Answer<T> argument(final int pos, Class<T> clazz) {
        return (Answer<T>) new ReturnsArgumentAt(pos);
    }

    public static MediaObject anyMediaObject() {
        return any(MediaObject.class);
    }

    public static Program anyProgram() {
        return any(Program.class);
    }

    public static Group anyGroup() {
        return any(Group.class);
    }

    public static Segment anySegment() {
        return any(Segment.class);
    }

    public static Schedule anySchedule() {
        return any(Schedule.class);
    }
}
