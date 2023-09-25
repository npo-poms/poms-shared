package nl.vpro.npoplayer;

import org.junit.jupiter.api.Test;
import org.meeuw.math.time.TestClock;

import static org.assertj.core.api.Assertions.assertThat;

class NpoPlayer9Test {

    NpoPlayer9 npoPlayer9 = new NpoPlayer9(
        "vpro",
        "mynicekeymynicekeymynicekeymynicekey")
        .withClock(TestClock.twentyTwenty());

    @Test
    public void test() {
        String token = npoPlayer9.token("MID_123");
        assertThat(token).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNSURfMTIzIiwiaWF0IjoxNTgyMjI2NDAwLCJpc3MiOiJ2cHJvIn0.kEvEZf4H7-EvK1psqnYZckrLZeqt7PoP-PcQ3-ZO21k");
    }

}
