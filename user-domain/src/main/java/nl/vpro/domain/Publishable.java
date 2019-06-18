package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface Publishable<T extends Publishable<T>> extends MutableEmbargo<T>, Accountable {
}
