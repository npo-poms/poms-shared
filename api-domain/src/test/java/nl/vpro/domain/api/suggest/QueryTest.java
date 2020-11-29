package nl.vpro.domain.api.suggest;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest {

    @Test
    public void testCleanup() {
        assertThat(new Query("Daan+Roosegaarde+bla").getText()).isEqualTo("daan roosegaarde bla");
        assertThat(new Query("123").getText()).isEqualTo("123");
        assertThat(new Query("eĥoŝanĝo ĉiuĵaŭde").getText()).isEqualTo("eĥoŝanĝo ĉiuĵaŭde");
        assertThat(new Query("ελενικα").getText()).isEqualTo("ελενικα");
        assertThat(new Query("日本語").getText()).isEqualTo("日本語");
        assertThat(new Query("123 - () abc").getText()).isEqualTo("123 abc");
    }
}
