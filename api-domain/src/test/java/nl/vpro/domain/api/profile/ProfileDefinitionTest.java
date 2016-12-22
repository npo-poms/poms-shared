/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.constraint.PredicateTestResult;
import nl.vpro.domain.constraint.media.Filter;
import nl.vpro.domain.constraint.media.MediaConstraints;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.assertj.core.api.Java6Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class ProfileDefinitionTest {

    @Before
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testGetNullValues() throws Exception {
        ProfileDefinition in = new ProfileDefinition();
        assertThat(in.getFilter()).isNull();
    }

    @Test
    public void testGetFilter() throws Exception {
        ProfileDefinition<MediaObject> in = new ProfileDefinition<>(new Filter(), null);
        ProfileDefinition out = JAXBTestUtil.roundTrip(in,
            "<media:filter/>");
        assertThat(out.getFilter()).isNotNull();
        assertThat(out.getFilter()).isInstanceOf(AbstractFilter.class);
    }


    @Test
    public void testApply() throws Exception {
        Filter filter = new Filter();
        filter.setConstraint(MediaConstraints.alwaysFalse());
        ProfileDefinition<MediaObject> in = new ProfileDefinition<>(filter, null);
        in.setProfile(new Profile("test"));


        PredicateTestResult r = in.testWithReason(MediaTestDataBuilder.broadcast().build());

        Java6Assertions.assertThat(r.applies()).isFalse();
        Java6Assertions.assertThat(r.getDescription().getValue()).isEqualTo("Never matches");
        Java6Assertions.assertThat(r.getReason()).isEqualTo("AlwaysFalse");
    }
}
