/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import java.io.StringReader;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class HasLocationConstraintTest {

    @Test
    public void testGetValue() {
        HasLocationConstraint in = new HasLocationConstraint();
        in.setPlatform(Platform.INTERNETVOD.name());
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:hasLocationConstraint platform=\"INTERNETVOD\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>");
    }

    @Test
    public void testApplyTrue() {
        Program program = MediaTestDataBuilder.program().mid("mid_1234").withLocations().build();
        assertThat(new HasLocationConstraint().test(program)).isTrue();
        assertThat(new HasLocationConstraint().testWithReason(program).getDescription(Locales.DUTCH)).isEqualTo("mid_1234 heeft een bron");

    }

    @Test
    public void testNoneFalse() {
        HasLocationConstraint constraint = new HasLocationConstraint();
        constraint.setPlatform("NONE");
        Program program = MediaTestDataBuilder.program().mid("mid_123").authoritativeRecord(Platform.INTERNETVOD).locations(new Location("http://foo/bar", OwnerType.BROADCASTER, Platform.INTERNETVOD)).build();
        assertThat(constraint.test(program)).isFalse();
        assertThat(constraint.testWithReason(program).getDescription(Locales.DUTCH)).isEqualTo("mid_123 heeft geen bron (NONE)");

    }

    @Test
    public void testNoneTrue() {
        HasLocationConstraint constraint = new HasLocationConstraint();
        constraint.setPlatform("NONE");
        Location location = new Location("http://foo/bar", OwnerType.BROADCASTER);
        location.setPlatform(null);
        Program program = MediaTestDataBuilder.program().authoritativeRecord(Platform.INTERNETVOD).locations( location).build();
        assertThat(constraint.test(program)).isTrue();
    }

    @Test
    public void testPlatformTrue() {
        HasLocationConstraint constraint = new HasLocationConstraint();
        constraint.setPlatform(Platform.INTERNETVOD.name());
        Program program = MediaTestDataBuilder.program().mid("mid_123").authoritativeRecord(Platform.INTERNETVOD).locations(new Location("http://foo/bar", OwnerType.BROADCASTER, Platform.INTERNETVOD)).build();
        assertThat(constraint.test(program)).isTrue();
        assertThat(constraint.testWithReason(program).getDescription(Locales.DUTCH)).isEqualTo("mid_123 heeft een bron (INTERNETVOD)");
    }

    @Test
    public void testPlatformFalse() {
        HasLocationConstraint constraint = new HasLocationConstraint();
        constraint.setPlatform(Platform.INTERNETVOD.name());
        Location location = new Location("http://foo/bar", OwnerType.BROADCASTER);
        location.setPlatform(null);
        Program program = MediaTestDataBuilder.program()
            .authoritativeRecord(Platform.INTERNETVOD)
            .locations( location)
            .build();
        assertThat(constraint.test(program)).isFalse();
    }

    @Test
    public void testApplyTrueBackwards() {
        Program program1 = MediaTestDataBuilder.program().withLocations().build();

        Program program2 = MediaTestDataBuilder.program().authoritativeRecord(Platform.INTERNETVOD).locations(new Location("http://foo/bar", OwnerType.BROADCASTER, Platform.INTERNETVOD)).build();

        HasLocationConstraint hasLocationConstraint = JAXB.unmarshal(new StringReader("<local:hasLocationConstraint xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>"), HasLocationConstraint.class);
        assertThat(hasLocationConstraint.test(program1)).isTrue();
        assertThat(hasLocationConstraint.test(program2)).isTrue();
    }

    @Test
    public void testApplyFalse() {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new HasLocationConstraint().test(program)).isFalse();
    }

    @Test
    public void testGetESPath() {
        assertThat(new HasLocationConstraint().getESPath()).isEqualTo("locations.urn");
    }

    @Test
    public void testGetESPathPlatform() {
        HasLocationConstraint locationConstraint = new HasLocationConstraint();
        locationConstraint.setPlatform(Platform.INTERNETVOD.name());
        assertThat(locationConstraint.getESPath()).isEqualTo("locations.platform");
    }
}
