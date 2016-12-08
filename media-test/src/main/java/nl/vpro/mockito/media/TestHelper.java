/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media;

import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.media.*;
import nl.vpro.mockito.media.answer.FirstArgument;
import nl.vpro.mockito.media.matcher.*;

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
        return ArgumentMatchers.argThat(new IsAnyMediaObject());
    }

    public static Program anyProgram() {
        return ArgumentMatchers.argThat(new IsAnyProgram());
    }

    public static Group anyGroup() {
        return ArgumentMatchers.argThat(new IsAnyGroup());
    }

    private static Segment anySegment() {
        return ArgumentMatchers.argThat(new IsAnySegment());
    }

    public static Schedule anySchedule() {
        return ArgumentMatchers.argThat(new IsAnySchedule());
    }
}
