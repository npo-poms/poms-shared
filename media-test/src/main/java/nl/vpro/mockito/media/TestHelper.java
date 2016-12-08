/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.media.*;
import nl.vpro.mockito.media.answer.FirstArgument;

import static org.mockito.ArgumentMatchers.any;

public class TestHelper {

    public static <T> FirstArgument firstArgument(Class<T> clazz) {
        return new FirstArgument<T>();
    }

    public static FirstArgument withSameMediaObject() {
        return new FirstArgument<MediaObject>();
    }

    public static FirstArgument withSameSchedule() {
        return new FirstArgument<Schedule>();
    }

    public static <T> Answer argument(final int pos, Class<T> clazz) {
        return new Answer<T>() {
            @Override
            public T answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (T)args[pos];
            }
        };
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
