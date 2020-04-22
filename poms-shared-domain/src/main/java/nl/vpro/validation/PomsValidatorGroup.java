package nl.vpro.validation;

/**
 * Should normally be validated, but not by hibernate. E.g. embargos could be checked for internal consistency (stop >
 * start), but those things don't matter for the database, and are not really a problem.
 *
 * @author Michiel Meeuwissen
 * @since 5.2.1
 */
public interface PomsValidatorGroup {
}
