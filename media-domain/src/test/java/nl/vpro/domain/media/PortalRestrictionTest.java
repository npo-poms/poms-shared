package nl.vpro.domain.media;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.user.Portal;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

public class PortalRestrictionTest {

    @Test
    public void testTypeJsonRoundTrip() throws Exception {
        PortalRestriction input = new PortalRestriction(new Portal("PORTAL_ID", "DISPLAY_NAME"));
        Jackson2Mapper mapper = Jackson2Mapper.INSTANCE;
        String jsonString = mapper.writeValueAsString(input);
        PortalRestriction output = mapper.readerFor(PortalRestriction.class).readValue(jsonString);
        assertThat(output.getPortal().getId()).isEqualTo("PORTAL_ID");
        assertThat(output.getPortal().getDisplayName()).isNull(); // Display name is not serialized to JSON
    }

    @Test
    public void testBuilder() {

        PortalRestriction input = PortalRestriction
            .builder()
            .portal(Portal.builder().id("PORTAL_ID").displayName("DISPLAY_NAME").build())
            .publishStart(LocalDate.of(2017, 5, 9).atStartOfDay())
            .build();
        assertThat(input.getStart().atZone(Schedule.ZONE_ID).toLocalDate()).isEqualTo(LocalDate.of(2017, 5, 9));


    }
}
