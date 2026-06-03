package nl.vpro.npoplayer9;

import org.junit.jupiter.api.Test;
import org.meeuw.time.TestClock;

import static org.assertj.core.api.Assertions.assertThat;

class TokenFactoryTest {

    TokenFactory npoPlayer9 = new TokenFactory(
        "vpro",
        "123123123123123123123123123123123123123123123")
        .withClock(TestClock.twentyTwenty());

    @Test
    public void test() {
        String token = npoPlayer9.token("mid_123");
        assertThat(token).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtaWRfMTIzIiwiaWF0IjoxNTgyMjI2NDAwLCJpc3MiOiJ2cHJvIn0.8tPo7XlEWpvtChBZgx8WOalprRHqypSoQsCyY2baB1w");
    }

}
