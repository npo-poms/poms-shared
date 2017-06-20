package nl.vpro.domain.constraint;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public interface WildTextConstraint<T> extends TextConstraint<T> {

    enum CaseHandling {
        ASIS,
        LOWER,
        UPPER,
        BOTH
    }

    default boolean isExact() {
        return true;
    }

    /**
     * The value used in wildcard queries. On default this implicetely adds stars, which will trigger an actual wildcard query.
     * If no stars are found, a prefix query is supposed.
     */
    default String getWildcardValue() {
        return "*" + getValue() + "*";
    }

    default CaseHandling getCaseHandling() {
        return CaseHandling.ASIS;
    }
}
