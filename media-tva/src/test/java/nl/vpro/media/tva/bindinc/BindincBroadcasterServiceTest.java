package nl.vpro.media.tva.bindinc;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.user.Broadcaster;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.33
 */
class BindincBroadcasterServiceTest {

    @Test
    public void find() {
        BindincBroadcasterService bs = new BindincBroadcasterService("https://poms.omroep.nl/broadcasters/");

        assertThat(bs.find("BNNVARA")).isNotNull();

        assertThat(bs.find("VER")).isEqualTo(new Broadcaster(""));

        assertThat(bs.find("OMROP FRYSLAN")).isEqualTo(new Broadcaster("ROFR", "Omrop Fryslân"));

    }


}
