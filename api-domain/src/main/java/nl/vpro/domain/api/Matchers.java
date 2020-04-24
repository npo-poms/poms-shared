package nl.vpro.domain.api;


import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.apache.commons.lang3.StringUtils;


/**
 * Utilities related to matchers.
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Matchers {

    static final MatchType TEXT = new MatchType() {
        @Override
        public boolean eval (String value, String input, boolean caseSensitive){
            return value == null ? input == null : (caseSensitive ? value.equals(input) : value.equalsIgnoreCase(input));
        }

        @Override
        public Validation valid (String value){
            return Validation.valid();
        }
    };
    static final MatchType REGEX = new MatchType() {

        @Override
        public boolean eval (String value, String input,boolean caseSensitive) {
            return value == null ? input == null : input != null && Pattern.compile(value, (caseSensitive ? 0 : Pattern.CASE_INSENSITIVE)).matcher(input).matches();
        }

        @Override
        public Validation valid (String value) {
            if (StringUtils.isNotBlank(value)) {
                try {
                    Pattern.compile(value);
                } catch (PatternSyntaxException pe) {
                    return Validation.invalid("Invalid regex: " + value + " " + pe.getMessage());
                }
                java.util.regex.Matcher matcher = REGEX_PATTERN.matcher(value);
                if (matcher.find()) {
                    return Validation.invalid("Regular expression can not start with a special character");
                }
            }
            return Validation.valid();
        }
    };
    static final MatchType WILDCARD = new MatchType() {
        @Override
        public boolean eval(String value, String input, boolean caseSensitive) {
            return value == null ? input == null : input != null && getWildcardPattern(value, caseSensitive).matcher(input).matches();

        }

        @Override
        public Validation valid(String value) {
            if (StringUtils.isNotBlank(value)) {
                if (value.startsWith("?") || value.startsWith("*")) {
                    return Validation.invalid("Wildcard can not start with either ? or *");
                }
            }
            return Validation.valid();
        }
    };

    private Matchers() {
    }

    public static Predicate<String> tokenizedPredicate(final AbstractTextMatcher<?> m) {
        return new Predicate<String>() {
            private Set<String> tokenizedValue;


            @Override
            public boolean test(@Nullable String s) {
                final String value = m.getValue();
                if (s == null) {
                    return value == null;
                }
                Set<String> tokens = new HashSet<>(tokenize(s));
                for(String token : getTokenizedValue()) {
                    if(tokens.contains(token)) {
                        return m.getMatch() != Match.NOT;
                    }
                }
                return m.getMatch() == Match.NOT;
            }

            @Override
            public String toString() {
                return (m.getMatch() == Match.NOT ? "!=" : "=") + tokenize(m.getValue());
            }

            protected Set<String> getTokenizedValue() {
                if(tokenizedValue == null) {
                    tokenizedValue = new HashSet<>(tokenize(m.getValue()));
                }
                return tokenizedValue;
            }

            protected Collection<String> tokenize(String string) {
                if(string == null) {
                    return Collections.emptyList();
                }
                List<String> list = new ArrayList<>(Arrays.asList(string.toLowerCase().split("\\s*\\b\\s*")));
                list.removeIf(StringUtils::isEmpty);
                return list;

            }

        };
    }


    protected static <S extends MatchType> Predicate<String> listPredicate(
        final Iterable<? extends AbstractTextMatcher<S>> textMatchers, final boolean tokenized) {
        return listPredicate(textMatchers, input -> {
            if(tokenized) {
                return Matchers.tokenizedPredicate(input);
            } else {
                return input;
            }
        });
    }

    public static <S extends MatchType> Predicate<String> listPredicate(
        final Iterable<? extends AbstractTextMatcher<S>>  textMatchers) {
        return listPredicate(textMatchers, false);
    }

    public static <S extends MatchType> Predicate<String> tokenizedListPredicate(
        final Iterable<? extends AbstractTextMatcher<S>>  textMatchers) {
        return listPredicate(textMatchers, true);
    }

    protected static <S extends MatchType> Predicate<String> listPredicate(
        final Iterable<? extends AbstractTextMatcher<S>> textMatchers,
        final Function<AbstractTextMatcher<?>, Predicate<String>> predicater) {
        if(textMatchers == null) {
            return a -> true;
        }
        return input -> {
            boolean hasShould = false;
            boolean shouldResult = false;

            for(AbstractTextMatcher<?> t : textMatchers) {
                boolean match = predicater.apply(t).test(input);
                switch(t.getMatch()) {
                    case NOT:
                        // fall through
                    case MUST:
                        if (! match) {
                            return false;
                        }
                        break;
                    case SHOULD:
                        hasShould = true;
                        shouldResult |= match;
                        break;
                }
            }
            return ! hasShould || shouldResult;
        };
    }


    /**
     * Creates a Predicate for a <em>collection</em> of values.
     */
    public static <V, C> Predicate<Collection<C>> toCollectionPredicate(
        @Nullable final Iterable<? extends Matcher<V>> matchers,
        @NonNull  final Function<C, V> valueGetter) {
        if(matchers == null) {
            return i -> true;
        }
        return collection -> {
            if(collection == null) {
                collection = Collections.emptyList();
            }
            boolean hasShould = false;
            boolean shouldResult = false;

            for (Matcher<V> t : matchers) {
                boolean match;
                switch (t.getMatch()) {
                    case NOT:
                        match = matchesAll(collection, t, valueGetter);
                        if (! match) {
                            return false;
                        }
                        break;
                    case MUST:
                        match = matchesOne(collection, t, valueGetter);
                        if (!match) {
                            return false;
                        }
                        break;
                    case SHOULD:
                        match = matchesOne(collection, t, valueGetter);
                        hasShould = true;
                        shouldResult |= match;
                        break;
                }
            }
            return ! hasShould || shouldResult;

        };
    }

    private static <C, V> boolean matchesOne(Collection<C> collection, Matcher<V> t,  Function<C, V> valueGetter) {
        for (C item : collection) {
            V value = valueGetter.apply(item);
            if (t.test(value)) {
                return true;
            }
        }
        return false;
    }
    private static <C, V> boolean matchesAll(Collection<C> collection, Matcher<V> t,  Function<C, V> valueGetter) {
        for (C item : collection) {
            V  value = valueGetter.apply(item);
            if (!t.test(value)) {
                return false;
            }
        }
        return true;
    }


}



