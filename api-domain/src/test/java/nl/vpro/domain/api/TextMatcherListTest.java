package nl.vpro.domain.api;

import java.util.Arrays;

import jakarta.validation.*;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.8
 */
public class TextMatcherListTest {

    @Test
    void marshal() {
        final TextMatcherList textMatcherList = new TextMatcherList(
                Arrays.asList(new TextMatcher("a", Match.SHOULD), new TextMatcher("b", Match.SHOULD)), Match.MUST);
        TextMatcherList result = JAXBTestUtil.roundTripAndSimilar(textMatcherList,
            """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <local:textMatcherList match="MUST" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" xmlns:local="uri:local">
                    <api:matcher match="SHOULD">a</api:matcher>
                    <api:matcher match="SHOULD">b</api:matcher>
                </local:textMatcherList>""");

        assertThat(result.asList().getFirst().getMatch()).isEqualTo(Match.SHOULD);
    }

    @Test
    void validate() {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            {
                final TextMatcherList validList = new TextMatcherList(
                    Arrays.asList(new TextMatcher("a", Match.SHOULD), new TextMatcher("b", Match.SHOULD)), Match.MUST);
                assertThat(validator.validate(validList)).isEmpty();
            }
            {
                final TextMatcherList invalidList = new TextMatcherList(
                    Arrays.asList(new TextMatcher("", Match.SHOULD), new TextMatcher("b", Match.SHOULD)), Match.MUST);
                var errors = assertThat(validator.validate(invalidList)).hasSize(1).actual();
                assertThat(errors.iterator().next().getMessage()).contains("must not be empty");
            }
        }
    }

}
