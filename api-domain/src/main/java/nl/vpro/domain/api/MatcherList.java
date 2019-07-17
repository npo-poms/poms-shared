package nl.vpro.domain.api;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Michiel Meeuwissen
 * @since 2.8
 */
public abstract class MatcherList<T> implements Iterable<T> {

    public static final Match DEFAULT_MATCH = Match.MUST;

    @XmlAttribute
    protected Match match = DEFAULT_MATCH;

    protected MatcherList() {

    }

    protected MatcherList(Match m) {
        this.match = m == null ? this.match : m;
    }

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


}
