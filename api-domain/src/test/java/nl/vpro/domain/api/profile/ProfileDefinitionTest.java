/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.constraint.PredicateTestResult;
import nl.vpro.domain.constraint.media.Filter;
import nl.vpro.domain.constraint.media.MediaConstraints;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class ProfileDefinitionTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testGetNullValues() {
        ProfileDefinition<?> in = new ProfileDefinition<MediaObject>();
        assertThat(in.getFilter()).isNull();
    }

    @Test
    public void testGetFilter() {
        ProfileDefinition<MediaObject> in = new ProfileDefinition<>(new Filter(), null);
        ProfileDefinition<MediaObject> out = JAXBTestUtil.roundTripAndSimilar(in,
            "<media:filter/>");
        assertThat(out.getFilter()).isNotNull();
        assertThat(out.getFilter()).isInstanceOf(AbstractFilter.class);
    }


    @Test
    public void testApply() {
        Filter filter = new Filter();
        filter.setConstraint(MediaConstraints.alwaysFalse());
        ProfileDefinition<MediaObject> in = new ProfileDefinition<>(filter, null);
        in.setProfile(new Profile("test"));


        PredicateTestResult r = in.testWithReason(MediaTestDataBuilder.broadcast().build());

        assertThat(r.applies()).isFalse();
        assertThat(r.getDescription().getValue()).isEqualTo("Never matches");
        assertThat(r.getReason()).isEqualTo("AlwaysFalse");
    }
}
