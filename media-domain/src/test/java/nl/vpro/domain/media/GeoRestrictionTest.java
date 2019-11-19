package nl.vpro.domain.media;

import org.junit.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public class GeoRestrictionTest {
    @Test
    public void xml() {

        GeoRestriction restriction = GeoRestriction.builder()
            .region(Region.EUROPE)
            .build();
        JAXBTestUtil.roundTripAndSimilar(restriction, "<local:geoRestriction regionId=\"EUROPE\" platform=\"INTERNETVOD\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\"/>");
    }

    @Test
    public void xmlEuropa() {

        GeoRestriction restriction = JAXBTestUtil.unmarshal("<local:geoRestriction regionId=\"EUROPA\" platform=\"INTERNETVOD\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\" />", GeoRestriction.class);
        assertThat(restriction.getRegion()).isNull();
    }

}
