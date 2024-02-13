package nl.vpro.domain.constraint;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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


    @BeforeEach
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

        PredicateTestResult rounded = JAXBTestUtil.roundTripAndSimilar(result, """
            <local:simplePredicateTestResult applies="false" reason="AlwaysFalse" xmlns:constraint="urn:vpro:api:constraint:2014" xmlns:local="uri:local">
                <constraint:description xml:lang="en_US">Never matches</constraint:description>
            </local:simplePredicateTestResult>""");

        System.out.println(rounded.toString());

    }

    @Test
    public void json() {
        log.info("{}", Locale.getDefault());

        PredicateTestResult result = Constraints.<String>alwaysFalse()
            .testWithReason("bla");

        log.info("{}", Locale.getDefault());

        Jackson2TestUtil.roundTripAndSimilar(result, """
            {
              "objectType" : "simple",
              "reason" : "AlwaysFalse",
              "applies" : false,
              "description" : {
                "value" : "Never matches",
                "lang" : "en_US"
              }
            }""");

    }



}
