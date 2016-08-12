package nl.vpro.domain.page.update;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class PortalUpdateTest {

    @Test
    public void testToPortal() throws Exception {
        PortalUpdate update = new PortalUpdate("VPRONL", "http://www.vpro.nl");
        assertThat(update.toPortal().getId()).isEqualTo("VPRONL");
    }
}
