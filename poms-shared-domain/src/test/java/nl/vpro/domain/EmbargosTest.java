package nl.vpro.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public class EmbargosTest {


    @Test
    public void unrestricted() {
        Embargo<BasicEmbargo> unrestrictedInstant = Embargos.unrestrictedInstant();

        Embargo<BasicEmbargo> of = Embargos.of(unrestrictedInstant.asRange());
        assertThat(of.getPublishStartInstant()).isNull();
        assertThat(of.getPublishStopInstant()).isNull();
    }

}
