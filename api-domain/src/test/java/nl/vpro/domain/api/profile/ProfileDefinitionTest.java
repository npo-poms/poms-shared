/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
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
import nl.vpro.domain.constraint.page.*;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.page.Page;
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
        ProfileDefinition<MediaObject> in = new ProfileDefinition<>(new Filter());
        ProfileDefinition<MediaObject> out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <local:profileDefinition xmlns="urn:vpro:api:profile:2013" xmlns:shared="urn:vpro:shared:2009" xmlns:constraint="urn:vpro:api:constraint:2014" xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013" xmlns:m="urn:vpro:media:2009" xmlns:local="uri:local">
                    <media:filter/>
                </local:profileDefinition>""");
        assertThat(out.getFilter()).isNotNull();
        assertThat(out.getFilter()).isInstanceOf(AbstractFilter.class);
    }


    @Test
    public void testApply() {
        Filter filter = new Filter();
        filter.setConstraint(MediaConstraints.alwaysFalse());
        ProfileDefinition<MediaObject> in = new ProfileDefinition<>(filter);
        in.setProfile(new Profile("test"));


        PredicateTestResult r = in.testWithReason(MediaTestDataBuilder.broadcast().build());

        assertThat(r.applies()).isFalse();
        assertThat(r.getDescription().getValue()).isEqualTo("Never matches");
        assertThat(r.getReason()).isEqualTo("AlwaysFalse");
    }

    @Test
    void testPageProfileWithNot() {
        nl.vpro.domain.constraint.page.Filter pageFilter = new nl.vpro.domain.constraint.page.Filter();
        pageFilter.setConstraint(PageConstraints.and(
            new PortalConstraint("VPRONL"),
            new Not(new SectionConstraint("/vpronl/pers"))
        ));
        ProfileDefinition<Page> in = new ProfileDefinition<>(pageFilter);
        ProfileDefinition<Page> out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <?xml version="1.0" encoding="UTF-8"?><local:profileDefinition xmlns:local="uri:local" xmlns="urn:vpro:api:profile:2013" xmlns:shared="urn:vpro:shared:2009" xmlns:constraint="urn:vpro:api:constraint:2014" xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013" xmlns:m="urn:vpro:media:2009">
                    <page:filter>
                      <page:and>
                        <page:portal>VPRONL</page:portal>
                        <page:not>
                          <page:section>/vpronl/pers</page:section>
                        </page:not>
                      </page:and>
                    </page:filter>
                  </local:profileDefinition>
                 \s""");
        assertThat(out.getFilter()).isNotNull();
        assertThat(out.getFilter()).isInstanceOf(AbstractFilter.class);
    }
}
