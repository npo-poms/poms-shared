package nl.vpro.domain.media.bind;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.com.neovisionaries.i18n.CountryCode;
import nl.vpro.i18n.Locales;

import static org.assertj.core.api.Assertions.assertThat;

public class CountryCodeAdapterTest {

    @BeforeClass
    public static void setup() {
        Locale.setDefault(Locales.DUTCH);
    }


    @Test
    @Ignore
    public void wiki() throws Exception {
        CountryCodeAdapter.Code cca = new CountryCodeAdapter.Code();
        Map<String, CountryCode> result = new TreeMap<>();
        for (CountryCode cc : CountryCode.values()) {
            String a2 = cc.getAlpha2();
            String a3 = cc.getAlpha3();
            if (a2 != null) {
                result.put(a2, cca.unmarshal(a2));
            }
            if (a3 != null && ! a3.equals(a2)) {
                result.put(a3, cca.unmarshal(a3));
            }
        }
        // output sorted
        System.out.println("||code||name in english||assignment||");
        for (Map.Entry<String, CountryCode> e : result.entrySet()) {
            System.out.println("|" + e.getKey() + "|" + e.getValue().getName() + "|" + e.getValue().getAssignment() + "|");
        }
    }

    @Test
    public void finlandReserved() throws Exception {
        CountryCodeAdapter cca = new CountryCodeAdapter();
        assertThat(cca.marshal(CountryCode.SF).getName()).isEqualTo("Finland");
    }

    @Test
    public void finland() throws Exception {
        CountryCodeAdapter cca = new CountryCodeAdapter();
        assertThat(cca.marshal(CountryCode.FI).getName()).isEqualTo("Finland");
    }
}
