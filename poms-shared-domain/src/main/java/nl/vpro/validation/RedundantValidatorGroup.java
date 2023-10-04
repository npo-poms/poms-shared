package nl.vpro.validation;

/**
 * Marks a {@code javax.constraint} as basically redundant, because other validation will cover it
 * E.g. {@link @Size} is redundant if there is also a {@link @Pattern} constraint, or if it is on a 'titles' property and something like {@code HasTitle} is in effect.
 * <p>
 *
 * @author Michiel Meeuwissen
 * @since 7.8
 */
public interface RedundantValidatorGroup {
}
