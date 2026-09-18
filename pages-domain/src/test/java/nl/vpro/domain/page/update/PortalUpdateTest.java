package nl.vpro.domain.page.update;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PortalUpdateTest {

    @Test
    void toPortal() {
        PortalUpdate update = new PortalUpdate("VPRONL", "http://www.vpro.nl");
        assertThat(update.toPortal().getId()).isEqualTo("VPRONL");
    }
}
