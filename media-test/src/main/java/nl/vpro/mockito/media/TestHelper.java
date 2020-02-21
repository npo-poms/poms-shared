/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media;

import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.media.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;

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
        return or(isNull(), any(MediaObject.class));
    }

    public static Program anyProgram() {
        return or(isNull(), any(Program.class));
    }

    public static Group anyGroup() {
        return or(isNull(), any(Group.class));
    }

    public static Segment anySegment() {
        return or(isNull(), any(Segment.class));
    }


    public static String anyStringOrNull() {
        return or(isNull(), anyString());
    }

    public static Schedule anySchedule() {
        return or(isNull(), any(Schedule.class));
    }

}
