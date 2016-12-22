/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Region;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
public class GeoRestrictionConstraintTest {

    @Test
    public void testGetStringValue() throws Exception {
        GeoRestrictionConstraint in = new GeoRestrictionConstraint(Region.BENELUX);
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:geoRestrictionConstraint xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:local=\"uri:local\" xmlns:media=\"urn:vpro:api:constraint:media:2013\">BENELUX</local:geoRestrictionConstraint>");
    }

    @Test
    public void testGetESPath() throws Exception {
        assertThat(new GeoRestrictionConstraint().getESPath()).isEqualTo("regions");
    }

    @Test
    public void testApplyWhenTrue() throws Exception {
        Program program = MediaTestDataBuilder.program().withGeoRestrictions().build();
        assertThat(new GeoRestrictionConstraint(Region.BENELUX));
    }

    @Test
    public void testApplyWhenFalse() throws Exception {
        Program program = MediaTestDataBuilder.program().build();
        assertThat(new GeoRestrictionConstraint(Region.NL).test(program)).isFalse();
    }
}
