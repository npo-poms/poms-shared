package nl.vpro.domain.api;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;

import nl.vpro.util.IterableWrapper;

/**
 * Represents a list of {@link Matcher}, itself being a Matcher again.
 * @author Michiel Meeuwissen
 * @since 2.8
 */
public abstract class MatcherList<V, T extends Matcher<V>> implements
    IterableWrapper<T>, // not Iterable, because that makes Hibernate Validator consider this a 'container' (HV000271)
    Matcher<V> {

    public static final Match DEFAULT_MATCH = Match.MUST;

    @XmlAttribute
    protected Match match = DEFAULT_MATCH;

    protected MatcherList() {

    }

    protected MatcherList(Match m) {
        this.match = m == null ? this.match : m;
    }

    @Override
    public Match getMatch() {
        return match;
    }

    // cannot implement List because that'll confuse jaxb
    @Override
    public abstract List<T> asList();

    @Override
    public String toString() {
        return match + ":" + (asList() == null ? "null" : asList().toString());
    }


    @Override
    public boolean test(V s) {
        boolean hasShould = false;
        boolean shouldResult = false;

        for(T t : asList()) {
            boolean match = t.test(s);
            switch (t.getMatch()) {
                case NOT, MUST -> {
                    if (!match) {
                        return false;
                    }
                }
                case SHOULD -> {
                    hasShould = true;
                    shouldResult |= match;
                }
            }
        }
        return ! hasShould || shouldResult;
    }

}
