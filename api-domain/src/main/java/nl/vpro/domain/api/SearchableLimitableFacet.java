package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface SearchableLimitableFacet<F extends AbstractSearch, S extends AbstractSearch>  extends LimitableFacet<F>, SearchableFacet<F, S> {
}
