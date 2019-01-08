package nl.vpro.domain.media.bind;

import java.util.Locale;

import org.junit.Test;

import nl.vpro.i18n.LocalizedString;

import static org.assertj.core.api.Assertions.assertThat;

public class LocaleAdapterTest {


    @Test
    public void adaptXx() {
        assertThat(LocalizedString.adapt("xx")).isEqualTo(new Locale("zxx"));
    }
    @Test
    public void adaptCz() {
        assertThat(LocalizedString.adapt("cz")).isEqualTo(new Locale("cs"));
    }

    @Test
    public void adaptNl_NL() {
        assertThat(LocalizedString.adapt("nl_NL")).isEqualTo(new Locale("nl", "NL"));
    }
}
