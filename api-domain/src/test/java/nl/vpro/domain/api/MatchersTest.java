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
class MatchersTest {
    @Test
    void tokenizedPredicate() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isTrue();
    }

    @Test
    void tokenizedPredicate2() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo bar")).isTrue();
    }

    @Test
    void tokenizedPredicate3() {
        TextMatcher matcher = new TextMatcher("foo bar");
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isTrue();
    }

    @Test
    void tokenizedPredicate4() {
        TextMatcher matcher = new TextMatcher("foo bar");
        assertThat(Matchers.tokenizedPredicate(matcher).test("xxx")).isFalse();
    }

    @Test
    void tokenizedPredicateNot() {
        TextMatcher matcher = new TextMatcher("foo", Match.NOT);
        assertThat(Matchers.tokenizedPredicate(matcher).test("foo")).isFalse();
    }

    @Test
    void tokenizedPredicates() {
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
    void untokenizedPredicate() {
        TextMatcher matcher = new TextMatcher("foo");
        assertThat(matcher.test("foo")).isTrue();
        assertThat(matcher.test("Foo")).isFalse();
    }

    @Test
    void untokenizedPredicateNot() {
        TextMatcher matcher = new TextMatcher("foo", Match.NOT);
        assertThat(matcher.test("foo")).isFalse();
    }

    @Test
    void untokenizedLowercasePredicate() {
        ExtendedTextMatcher matcher = new ExtendedTextMatcher("foo", Match.MUST, StandardMatchType.TEXT, false);
        assertThat(matcher.test("foo")).isTrue();
        assertThat(matcher.test("Foo")).isTrue();
    }

    @Test
    void untokenizedPredicates() {
        TextMatcher matcher1 = TextMatcher.should("foo");
        TextMatcher matcher2 = TextMatcher.should("bar");
        assertThat(matcher2.test("foo")).isFalse();
        assertThat(Matchers.listPredicate(new TextMatcherList(matcher1, matcher2)).test("foo")).isTrue();
        matcher1.setMatch(Match.MUST);
        matcher2.setMatch(Match.MUST);
        assertThat(Matchers.listPredicate(new TextMatcherList(matcher1, matcher2)).test("foo")).isFalse();
    }

    @Test
    void listPredicateWithShould() {
        List<TextMatcher> textMatchers = Arrays.asList(
            TextMatcher.should("SEASON"),
            TextMatcher.should("SERIES")
        );
        assertThat(Matchers.listPredicate(textMatchers).test("SEASON")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("ALBUM")).isFalse();
    }

    @Test
    void listPredicateWithNots() {
        List<TextMatcher> textMatchers = Arrays.asList(
            TextMatcher.not("SEASON"),
            TextMatcher.not("SERIES")
        );
        //assertThat(Matchers.listPredicate(textMatchers).test("SEASON")).isFalse();
        //assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("ALBUM")).isTrue();
    }

    @Test
    void listPredicateWithMust() {
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("AA.*", StandardMatchType.REGEX), new TextMatcher(".*BB", StandardMatchType.REGEX));
        assertThat(Matchers.listPredicate(textMatchers).test("AAxxxxBBB")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("foobar")).isFalse();
        assertThat(Matchers.listPredicate(textMatchers).test("AAxxxx")).isFalse();
    }


    @Test
    void listPredicateWithShoulds() {
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST,
            TextMatcher.should("BROADCAST", StandardMatchType.TEXT),
            TextMatcher.should("SEGMENT", StandardMatchType.TEXT)
        );
        assertThat(Matchers.listPredicate(textMatchers).test("BROADCAST")).isTrue();
        assertThat(Matchers.listPredicate(textMatchers).test("SERIES")).isFalse();
    }

    @Test
    void listPredicateWithOneNot() {
        Function<String, String> STRING = input -> input;
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB", Match.NOT));
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isTrue();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isFalse();

        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(null)).isTrue();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.emptyList())).isTrue();
    }

    @Test
    void listPredicateWithOne() {
        Function<String, String> STRING = input -> input;
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB"));
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isTrue();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isTrue();

        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(null)).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.emptyList())).isFalse();
    }

    @Test
    void listPredicateWithMore() {
        Function<String, String> STRING = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input;

            }
        };
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB", Match.NOT), new TextMatcher("CC", Match.NOT));
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isTrue();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Arrays.asList("AA", "BB"))).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Arrays.asList("CC", "BB"))).isFalse();

        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(null)).isTrue();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.emptyList())).isTrue();
    }

    @Test
    void listPredicateWithMore2() {
        Function<String, String> STRING = new Function<String, String>() {
            @Nullable
            @Override
            public String apply(@Nullable String input) {
                return input;

            }
        };
        TextMatcherList textMatchers = new TextMatcherList(Match.MUST, new TextMatcher("BB"), new TextMatcher("CC", Match.NOT));
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("BB"))).isTrue();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.singletonList("AA"))).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Arrays.asList("CC", "BB"))).isFalse();

        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(null)).isFalse();
        assertThat(Matchers.toCollectionPredicate(textMatchers, STRING).test(Collections.emptyList())).isFalse();
    }
}
