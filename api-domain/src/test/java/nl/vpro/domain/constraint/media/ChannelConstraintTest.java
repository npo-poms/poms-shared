/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class ChannelConstraintTest {

    @Test
    public void testGetStringValue() {
        ChannelConstraint in = new ChannelConstraint(Channel.NED1);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:channelConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">NED1</local:channelConstraint>\n\n");
    }

    @Test
    public void testGetESPath() {
        assertThat(new ChannelConstraint().getESPath()).isEqualTo("scheduleEvents.channel");
    }

    @Test
    public void testApplyWhenTrue() {
        Program program = MediaTestDataBuilder.program().withScheduleEvents().build();
        assertThat(new ChannelConstraint(Channel.NED3).test(program)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Program program = MediaTestDataBuilder.program().withAuthorityRecord().build();
        assertThat(new ChannelConstraint(Channel.ASIA).test(program)).isFalse();
    }
}
