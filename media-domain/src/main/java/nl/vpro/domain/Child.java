package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface Child<T> {

    void setParent(T mo);

    T getParent();
}
