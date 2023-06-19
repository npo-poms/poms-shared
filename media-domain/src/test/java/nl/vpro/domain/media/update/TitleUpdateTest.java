package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

class TitleUpdateTest {

    @Test
    public void json() {
        TitleUpdate up = TitleUpdate.main("hoi");
        up.isEmpty();
        Jackson2TestUtil.roundTripAndSimilar(
            TitleUpdate.main("hoi"),
            """
                {
                  "value" : "hoi",
                  "type" : "MAIN"
                }"""
        );
    }

}
