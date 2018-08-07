package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface SearchableLimitableFacet<T extends AbstractSearch, S extends AbstractSearch>  extends LimitableFacet<T>, SearchableFacet<T, S> {
}
