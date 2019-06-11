package nl.vpro.domain.media.bind;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.meeuw.i18n.Country;
import org.meeuw.i18n.CurrentCountry;
import org.meeuw.i18n.Region;
import org.meeuw.i18n.Regions;
import org.meeuw.i18n.bind.jaxb.Code;

import com.neovisionaries.i18n.CountryCode;

import nl.vpro.i18n.Locales;

import static org.assertj.core.api.Assertions.assertThat;

public class CountryCodeAdapterTest {

    @BeforeClass
    public static void setup() {
        Locale.setDefault(Locales.DUTCH);
    }


    @Test
    @Ignore
    public void wiki() {
        Code cca = new Code();
        Map<String, Region> result = new TreeMap<>();
        Regions.values().filter(Country.CURRENT_AND_FORMER_AND_USER).forEach((c) -> {
            result.put(c.getCode(), cca.unmarshal(c.getCode()));
            if (c instanceof Country) {
                Country cc = (Country) c;
                String a2 = cc.getAlpha2();
                String a3 = cc.getAlpha3();
                if (a2 != null) {
                    result.put(a2, cca.unmarshal(a2));
                }
                if (a3 != null && ! a3.equals(a2)) {
                    result.put(a3, cca.unmarshal(a3));
                }
            }
        });
        // output sorted
        System.out.println("||code||name in english||assignment||");
        for (Map.Entry<String, Region> e : result.entrySet()) {
            Region c = e.getValue();
            CountryCode.Assignment a = (c instanceof CurrentCountry) ? ((CurrentCountry) c).getAssignment() : null;
            System.out.println("|" + e.getKey() + "|" + e.getValue().getName() + "|" + a + "|");
        }
    }

    @Test
    public void finlandReserved() {
        CountryCodeAdapter cca = new CountryCodeAdapter();
        assertThat(cca.marshal(new CurrentCountry(CountryCode.SF)).getName()).isEqualTo("Finland");
    }

    @Test
    public void finland() {
        CountryCodeAdapter cca = new CountryCodeAdapter();
        assertThat(cca.marshal(new CurrentCountry(CountryCode.FI)).getName()).isEqualTo("Finland");
    }
}
