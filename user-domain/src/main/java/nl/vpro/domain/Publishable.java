package nl.vpro.domain;

/**
 * A publishable is {@link MutableEmbargo} and {@link Accountable}.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface Publishable<T extends Publishable<T>> extends MutableEmbargo<T>, Accountable {
}
