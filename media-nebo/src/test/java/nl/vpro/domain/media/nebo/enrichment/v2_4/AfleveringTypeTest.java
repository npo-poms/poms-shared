package nl.vpro.domain.media.nebo.enrichment.v2_4;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 */
public class AfleveringTypeTest {

    @Test
    public void validate() {
        assertEquals("http://spuitenenslikken.bnn.nl/",
            AfleveringType.validate("HTTP://spuitenenslikken.bnn.nl/"));

        assertEquals("https://spuitenenslikken.bnn.nl/",
            AfleveringType.validate("HTTPS://spuitenenslikken.bnn.nl/"));

        assertEquals("http://tweevoortwaalf.vara.nl",
            AfleveringType.validate("tweevoortwaalf.vara.nl"));

        assertEquals("http://tweevoortwaalf.vara.nl",
            AfleveringType.validate("http://tweevoortwaalf.vara.nl."));

        assertEquals("ftp://spuitenenslikken.bnn.nl/",
            AfleveringType.validate("ftp://spuitenenslikken.bnn.nl/"));

    }

}
