package nl.vpro.validation;

/**
 * Marker interface for validation messages that must be logged as a WARNING. Enforcing these violations is not feasible. It would e.g. be nice to enforce an gtaaURI, but it probably is not realistic to expect that all clients will be able to do that.
 * <p>
 * Currently, only implemented in backend rest api.
 *
 * @author Michiel Meeuwissen
 * @since 7.7
 */
public interface WeakWarningValidatorGroup {
}
