package nl.vpro.domain.gtaa;

import lombok.extern.log4j.Log4j2;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class SchemeTest {

    @Test
    void displayableValues() {
        for (Scheme scheme : Scheme.displayableValues()) {
            log.info(scheme.name() + " : " + scheme.getDisplayName());
        }
        assertThat(Scheme.displayableValues()).hasSize(8);

        assertThat(Scheme.values()).hasSize(9);

    }
}
