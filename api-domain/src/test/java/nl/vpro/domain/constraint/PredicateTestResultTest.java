package nl.vpro.domain.constraint;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Locale;

import javax.xml.bind.JAXB;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
public class PredicateTestResultTest {


    @Before
    public void setup() {
        Locales.setDefault(Locale.US);
    }

    @Test
    public void and() {
        AbstractAnd<String> constraint = new AbstractAnd<String>(Constraints.alwaysTrue(), Constraints.alwaysFalse(), Constraints.alwaysFalse()) {
        };
        AndPredicateTestResult result = constraint.testWithReason("foobar");

        assertThat(result.getReason()).isEqualTo("And");

        assertThat(result.getClauses()).hasSize(3);
        JAXB.marshal(result, System.out);
        assertThat(result.getDescription(Locale.US)).isEqualTo("(Never matches and Never matches)");
        //assertThat(result.getReasonDescription(Locale.US)[1]).isEqualTo("Never matches");
    }

    @Test
    public void or() {
        Constraint<String> constraint = new AbstractOr<String>(
            Constraints.alwaysFalse(),
            Constraints.alwaysFalse()
        ) {
        };
        PredicateTestResult result = constraint.testWithReason("foobar");

        assertThat(result.getReason()).isEqualTo("Or");
        assertThat(result.getDescription(Locale.US)).isEqualTo("(Never matches or Never matches should match)");
    }

    @Test
    public void xml() {
        PredicateTestResult result =  Constraints.<String>alwaysFalse().testWithReason("bla");

        PredicateTestResult rounded = JAXBTestUtil.roundTripAndSimilar(result, "<local:simplePredicateTestResult applies=\"false\" reason=\"AlwaysFalse\" xmlns:constraint=\"urn:vpro:api:constraint:2014\" xmlns:local=\"uri:local\">\n" +
            "    <constraint:description xml:lang=\"en_US\">Never matches</constraint:description>\n" +
            "</local:simplePredicateTestResult>");

        System.out.println(rounded.toString());

    }

    @Test
    public void json() {
        log.info("{}", Locale.getDefault());

        PredicateTestResult result = Constraints.<String>alwaysFalse()
            .testWithReason("bla");

        log.info("{}", Locale.getDefault());

        Jackson2TestUtil.roundTripAndSimilar(result, "{\n" +
            "  \"objectType\" : \"simple\",\n" +
            "  \"reason\" : \"AlwaysFalse\",\n" +
            "  \"applies\" : false,\n" +
            "  \"description\" : {\n" +
            "    \"value\" : \"Never matches\",\n" +
            "    \"lang\" : \"en_US\"\n" +
            "  }\n" +
            "}");

    }



}
