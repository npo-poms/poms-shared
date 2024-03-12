package nl.vpro.npoplayer9;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

class TokenRequestTest {


    @Test
    public void json() {
        TokenRequest request = Jackson2TestUtil.roundTripAndSimilar(new TokenRequest("mid_123"), """
                {
                    "mid" : "mid_123"
                  }
                """);
        Assertions.assertThat(request.getMid()).isEqualTo("mid_123");
    }

}
