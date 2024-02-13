package nl.vpro.domain.api;

import java.util.Iterator;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a list of {@link Matcher}, itself being a Matcher again.
 * @author Michiel Meeuwissen
 * @since 2.8
 */
public abstract class MatcherList<V, T extends Matcher<V>> implements Iterable<T>, Matcher<V> {

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

    public abstract List<T> asList();

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return asList().iterator();
    }

    // cannot implement List because that'll confuse jaxb
    public int size() {
        return asList().size();
    }

    public boolean isEmpty() {
        return asList().isEmpty();
    }

    // cannot implement List because that'll confuse jaxb
    public T get(int index) {
        return asList().get(index);
    }

    @Override
    public String toString() {
        return match + ":" + (asList() == null ? "null" : asList().toString());
    }


    @Override
    public boolean test(V s) {
        boolean hasShould = false;
        boolean shouldResult = false;

        for(T t : this) {
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
