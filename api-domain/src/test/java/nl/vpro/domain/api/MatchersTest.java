package nl.vpro.domain.api;

import java.util.*;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class MatchersTest {
    @Test
    public void testTokenizedPredicate() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isTrue();
    }

    @Test
    public void testTokenizedPredicate2() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo bar")).isTrue();
    }

    @Test
    public void testTokenizedPredicate3() {
        TextMatcher matcher = new TextMatcher("foo bar");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isTrue();
    }

    @Test
    public void testTokenizedPredicate4() {
        TextMatcher matcher = new TextMatcher("foo bar");
        assertThat(Matchers.tokenizedPredicate(matcher).test("xxx")).isFalse();
    }

    @Test
    public void testTokenizedPredicateNot() {
        TextMatcher matcher = new TextMatcher("foo", Match.NOT);
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isFalse();
    }

    @Test
    public void testTokenizedPredicates() {
        TextMatcher matcher1 = TextMatcher.must("foo");
        TextMatcher matcher2 = TextMatcher.must("bar");
        assertThat(Matchers.tokenizedListPredicate(Arrays.asList(matcher1, matcher2)).test("foo")).isFalse();
        matcher1.setMatch(Match.SHOULD);
        matcher2.setMatch(Match.SHOULD);
        assertThat(Matchers.tokenizedListPredicate(Arrays.asList(
            matcher1,
            matcher2
        )).test("foo")).isTrue();
    }

    @Test
    public void testUntokenizedPredicate() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(matcher.test("foo")).isTrue();
        assertThat(matcher.test("Foo")).isFalse();
    }

    @Test
    public void testUntokenizedPredicateNot() {
        TextMatcher matcher = new TextMatcher("foo", Match.NOT);
        assertThat(matcher.test("foo")).isFalse();
    }

    @Test
    public void testUntokenizedLowercasePredicate() {
        ExtendedTextMatcher matcher = new ExtendedTextMatcher("foo", Match.MUST, StandardMatchType.TEXT, false);
        assertThat(matcher.test("foo")).isTrue();
        assertThat(matcher.test("Foo")).isTrue();
    }

    @Test
    public void testUntokenizedPredicates() {
        TextMatcher matcher1 = new TextMatcher("foo");
        TextMatcher matcher2 = new TextMatcher("bar");
        assertThat(matcher2.test("foo")).isFalse();
        assertThat(Matchers.listPredicate(new TextMatcherList(Match.SHOULD, matcher1, matcher2)).test("foo")).isTrue();
        assertThat(Matchers.listPredicate(new TextMatcherList(matcher1, matcher2)).test("foo")).isFalse();
    }

    @Test
    public void testListPredicateWithShould() {
        List<TextMatcher> textMatchers = Arrays.asList(
            TextMatcher.should("SEASON"),
            TextMatcher.should("SERIES")
        );
        assertThat(Matchers.listPredicate(textMatchers).test("SEASON")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("ALBUM")).isFalse();
    }

    @Test
    public void testListPredicateWithNots() {
        List<TextMatcher> textMatchers = Arrays.asList(
            TextMatcher.not("SEASON"),
            TextMatcher.not("SERIES")
        );
        //assertThat(Matchers.listPredicate(textMatchers).test("SEASON")).isFalse();
        //assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("ALBUM")).isTrue();
    }

    @Test
    public void testListPredicateWithMust() {
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("AA.*", StandardMatchType.REGEX), new TextMatcher(".*BB", StandardMatchType.REGEX));
        assertThat(Matchers.listPredicate(textMatchers).test("AAxxxxBBB")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("foobar")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("AAxxxx")).isFalse();
    }


    @Test
    public void testListPredicateWithShoulds() {
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST,
            TextMatcher.should("BROADCAST", StandardMatchType.TEXT),
            TextMatcher.should("SEGMENT", StandardMatchType.TEXT)
        );
        assertThat(Matchers.listPredicate(textMatchers).test("BROADCAST")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isFalse();
    }

    @Test
    public void testListPredicateWithOneNot() {
        Function<String, String> STRING = input -> input;
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB", Match.NOT));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isFalse();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isTrue();
    }

    @Test
    public void testListPredicateWithOne() {
        Function<String, String> STRING = input -> input;
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB"));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isTrue();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isFalse();
    }

    @Test
    public void testListPredicateWithMore() {
        Function<String, String> STRING = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input;

            }
        };
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB", Match.NOT), new TextMatcher("CC", Match.NOT));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("CC", "BB"))).isFalse();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isTrue();
    }

    @Test
    public void testListPredicateWithMore2() {
        Function<String, String> STRING = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input;

            }
        };
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB"), new TextMatcher("CC", Match.NOT));
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isTrue();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Arrays.asList("CC", "BB"))).isFalse();

        assertThat(Matchers.toPredicate(textMatchers, STRING).test(null)).isFalse();
        assertThat(Matchers.toPredicate(textMatchers, STRING).test(Collections.emptyList())).isFalse();
    }
}
