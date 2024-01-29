package nl.vpro.domain.media.bind;

import java.util.*;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.junit.jupiter.api.*;
import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.countries.CurrentCountry;
import org.meeuw.i18n.regions.Region;
import org.meeuw.i18n.regions.RegionService;
import org.meeuw.i18n.regions.bind.jaxb.Code;
import org.meeuw.i18n.subdivisions.UserAssignedCountrySubdivision;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;

import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meeuw.i18n.countries.Country.*;

public class CountryCodeAdapterTest {

    @BeforeAll
    public static void setup() {
        Locale.setDefault(Locales.DUTCH);
    }


    @Test
    @Disabled
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

    public static class A {
        private List<Country> countries = new ArrayList<>();

        @XmlElement(name = "country")
        @JsonProperty("countries")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @XmlJavaTypeAdapter(value = CountryCodeAdapter.class)
        public List<Country> getCountries() {
            return countries;
        }

        public void setCountries(List<Country> countries) {
            this.countries = countries;
        }
    }

    A nl = new A();
    {
        nl.countries.add(Country.of(CountryCode.NL));

    }
    A empty = new A();
    @Test
    public void xml() {
        JAXBTestUtil.roundTripAndSimilar(nl, """
            <local:a xmlns:local="uri:local" xmlns="urn:vpro:media:2009">
                <country code="NL">Nederland</country>
            </local:a>""");
        JAXBTestUtil.roundTripAndSimilar(empty, "<local:a xmlns:local=\"uri:local\" xmlns=\"urn:vpro:media:2009\">\n" +
                "</local:a>");
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilar(nl, """
            {
              "countries" : [ {
                "code" : "NL",
                "value" : "Nederland"
              } ]
            }""");
        Jackson2TestUtil.roundTripAndSimilar(empty, "{}");
    }
}
