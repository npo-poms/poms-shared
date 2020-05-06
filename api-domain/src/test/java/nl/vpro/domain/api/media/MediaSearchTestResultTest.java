package nl.vpro.domain.api.media;

import lombok.ToString;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import nl.vpro.domain.api.Match;
import nl.vpro.util.Truthiness;

import static nl.vpro.util.Truthiness.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
public class MediaSearchTestResultTest {

    static MediaSearch.TestResult mustAndFalse = new MediaSearch.TestResultImpl("mustandfalse", Match.MUST, () -> false);
    static MediaSearch.TestResult mustAndTrue = new MediaSearch.TestResultImpl("mustandtrue", Match.MUST, () -> true);

    static MediaSearch.TestResult shouldAndFalse = new MediaSearch.TestResultImpl("shouldandfalse", Match.SHOULD, () -> false);
    static MediaSearch.TestResult shouldAndTrue  = new MediaSearch.TestResultImpl("shouldandtrue", Match.SHOULD, () -> true);

    static MediaSearch.TestResult notAndFalse = new MediaSearch.TestResultImpl("notandfalse", Match.NOT, () -> false);
    static MediaSearch.TestResult notAndTrue  = new MediaSearch.TestResultImpl("notandtrue", Match.NOT, () -> true);

    static MediaSearch.TestResult ignore  = MediaSearch.TestResultIgnore.INSTANCE;

    @ToString
    static class Case {
        final MediaSearch.TestResult [] impls;
        final Truthiness expected;

        Case(Truthiness expected, MediaSearch.TestResult... impls) {
            this.impls = impls;
            this.expected = expected;
        }
    }

    public static List<Case> getCases() {
        return Arrays.asList(
            new Case(TRUE),
            new Case(TRUE, ignore),
            new Case(FALSE, mustAndFalse, mustAndTrue),
            new Case(FALSE, mustAndFalse, mustAndTrue ,ignore),
            new Case(TRUE, mustAndTrue),
            new Case(TRUE, mustAndTrue, mustAndTrue),
            new Case(TRUE, mustAndTrue, mustAndTrue, ignore),
            new Case(TRUE, shouldAndTrue),
            new Case(MAYBE_NOT, shouldAndFalse),
            new Case(PROBABLY, shouldAndFalse, mustAndTrue),// perhaps TRUE?
            new Case(FALSE, shouldAndFalse, mustAndFalse)
        );

    }

    @ParameterizedTest
    @MethodSource("getCases")
    public void testResult(Case testCase) {
        assertThat(new MediaSearch.TestResultCombiner(testCase.impls).test()).isEqualTo(testCase.expected);
    }
}
