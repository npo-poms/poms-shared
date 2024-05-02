package nl.vpro.domain;

/**
 * Defines {@link #getParent()} and {@link #setParent(Object)} methods, logically living as a 'child' of some parent.
 * @author Michiel Meeuwissen
 * @since 5.5
 * @param <P> The type of the parent
 */
public interface Child<P> {

    void setParent(P mo);

    P getParent();
}
