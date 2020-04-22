package nl.vpro.domain.api.media;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.Match;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
public class MediaSearchTestResultTest {

    MediaSearch.TestResult mustAndFalse = new MediaSearch.TestResultImpl("mustandfalse", Match.MUST, () -> false);
    MediaSearch.TestResult mustAndTrue = new MediaSearch.TestResultImpl("mustandtrue", Match.MUST, () -> true);

    MediaSearch.TestResult shouldAndFalse = new MediaSearch.TestResultImpl("shouldandfalse", Match.SHOULD, () -> false);
    MediaSearch.TestResult shouldAndTrue  = new MediaSearch.TestResultImpl("shouldandtrue", Match.SHOULD, () -> true);

    MediaSearch.TestResult notAndFalse = new MediaSearch.TestResultImpl("notandfalse", Match.NOT, () -> false);
    MediaSearch.TestResult notAndTrue  = new MediaSearch.TestResultImpl("notandtrue", Match.NOT, () -> true);

    MediaSearch.TestResult ignore  = MediaSearch.TestResultIgnore.INSTANCE;



    @Test
    public void testResult() {
        assertThat(new MediaSearch.TestResultCombiner().test()).isTrue();
        assertThat(new MediaSearch.TestResultCombiner(ignore).test()).isTrue();

        assertThat(new MediaSearch.TestResultCombiner(mustAndFalse, mustAndTrue).test()).isFalse();
        assertThat(new MediaSearch.TestResultCombiner(mustAndFalse, mustAndTrue ,ignore).test()).isFalse();
        assertThat(new MediaSearch.TestResultCombiner(mustAndTrue).test()).isTrue();
        assertThat(new MediaSearch.TestResultCombiner(mustAndTrue, mustAndTrue).test()).isTrue();
        assertThat(new MediaSearch.TestResultCombiner(mustAndTrue, mustAndTrue, ignore).test()).isTrue();

    }
}
