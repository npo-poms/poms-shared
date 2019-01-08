/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class SearchFieldTest {

    @Test
    public void testGetNameAndBoost() throws Exception {
        SearchField in = new SearchField("title", 5f);
        SearchField out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:searchField boost=\"5.0\" xmlns=\"urn:vpro:api:profile:2013\" xmlns:local=\"uri:local\">title</local:searchField>");
        assertThat(out.getName()).isEqualTo("title");
        assertThat(out.getBoost()).isEqualTo(5f);
    }

    @Test
    public void testBoostOnDefaultValue() {
        assertThat(new SearchField("name").getBoost()).isEqualTo(1f);
    }
}
