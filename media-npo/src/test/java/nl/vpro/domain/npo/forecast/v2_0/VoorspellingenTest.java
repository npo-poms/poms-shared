package nl.vpro.domain.npo.forecast.v2_0;

import java.io.InputStream;

import jakarta.xml.bind.JAXB;

import nl.vpro.domain.npo.AuthorityPlatform;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8.6
 */
public class VoorspellingenTest {


    @Test
    public void unmarshal() {
        InputStream example = getClass().getClassLoader().getResourceAsStream("voorspel.xml");
        Voorspellingen voorspellingen = JAXB.unmarshal(example, Voorspellingen.class);

        assertThat(voorspellingen.getAflevering()).hasSize(2);
        assertThat(voorspellingen.getAflevering().get(0).getPrid()).isEqualTo("VPWON_1266972");
        assertThat(voorspellingen.getAflevering().get(0).getPlatform()).isEqualTo("internetvod");


    }

    @Test
    public void extra() {
        InputStream example = getClass().getClassLoader().getResourceAsStream("voorspel_extra.xml");

        Voorspellingen voorspellingen = JAXB.unmarshal(example, Voorspellingen.class);

        AuthorityPlatform platform = AuthorityPlatform.valueOf(voorspellingen.getAflevering()
                .get(0)
                .getPlatform()
                .toLowerCase()
        );

        assertThat(voorspellingen.getAflevering()).hasSize(2);
        assertThat(voorspellingen.getAflevering().get(0).getPrid()).isEqualTo("VPWON_1266972");
        assertThat(platform.equals("extra"));


    }

}
