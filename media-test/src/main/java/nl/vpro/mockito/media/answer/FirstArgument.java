/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media.answer;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FirstArgument<T> implements Answer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return (T)args[0];
    }
}
