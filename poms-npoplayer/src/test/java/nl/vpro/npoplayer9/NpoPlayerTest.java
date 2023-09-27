package nl.vpro.npoplayer9;

import org.junit.jupiter.api.Test;
import org.meeuw.math.time.TestClock;

import static org.assertj.core.api.Assertions.assertThat;

class NpoPlayerTest {

    NpoPlayer npoPlayer9 = new NpoPlayer(
        "vpro",
        "3777217A25432A462D4A614E635266556A586E3272357538782F413F4428472B")
        .withClock(TestClock.twentyTwenty());

    @Test
    public void test() {
        String token = npoPlayer9.token("mid_123");
        assertThat(token).isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODIyMjY0MDAsInN1YiI6Im1pZF8xMjMiLCJpc3MiOiJ2cHJvIn0.pUr8qDYd9hqc-zf8ftBk0aTAQpvgrnImU4Yom_G07p4");
    }

}
