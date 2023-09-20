/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;


import org.junit.jupiter.api.Test;

import nl.vpro.test.util.serialize.SerializeTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class MediaObjectSerializableTest {


    @Test
    public void program() throws Exception {

        Program program = MediaTestDataBuilder.program().withEverything().build();
        Program rounded = SerializeTestUtil.roundTripAndEquals(program);

    }


    @Test
    public void segment() throws Exception {

        Segment segment = MediaTestDataBuilder.segment().withEverything().build();
        SerializeTestUtil.roundTripAndEquals(segment);
    }


    @Test
    public void group() throws Exception {

        Group group = MediaTestDataBuilder.group().withEverything().build();
        SerializeTestUtil.roundTripAndEquals(group);
    }
}
