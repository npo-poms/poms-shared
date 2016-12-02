package nl.vpro.domain.media;

import org.junit.Test;

import nl.vpro.domain.user.Portal;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

public class PortalRestrictionTest {

    @Test
    public void testTypeJsonRoundTrip() throws Exception {
        PortalRestriction input = new PortalRestriction(new Portal("PORTAL_ID", "DISPLAY_NAME"));
        Jackson2Mapper mapper = Jackson2Mapper.INSTANCE;
        String jsonString = mapper.writeValueAsString(input);
        PortalRestriction output = mapper.reader(PortalRestriction.class).readValue(jsonString);
        assertThat(output.getPortal().getId()).isEqualTo("PORTAL_ID");
        assertThat(output.getPortal().getDisplayName()).isNull(); // Display name is not serialized to JSON
    }
}
