package nl.vpro.domain.media.bind;

import java.util.*;

import org.junit.*;
import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.countries.CurrentCountry;
import org.meeuw.i18n.regions.Region;
import org.meeuw.i18n.regions.RegionService;
import org.meeuw.i18n.regions.bind.jaxb.Code;
import org.meeuw.i18n.subdivisions.UserAssignedCountrySubdivision;

import com.neovisionaries.i18n.CountryCode;

import nl.vpro.i18n.Locales;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.countries.Country.*;

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
        RegionService.getInstance().values().filter(
            IS_OFFICIAL
                .or(IS_FORMER)
                .or(IS_USER_ASSIGNED)
            .or((c) -> c instanceof UserAssignedCountrySubdivision)
        ).forEach((c) -> {
            result.put(c.getCode(), cca.unmarshal(c.getCode()));
            if (c instanceof Country) {
                Country cc = (Country) c;
                String a2 = cc.getCode();
                String a3 = cc instanceof CurrentCountry ?  ((CurrentCountry) cc).getAlpha3() : null;
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
