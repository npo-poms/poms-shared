package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface NameableSearchableLimitableFacet<F extends AbstractSearch, S extends AbstractSearch>  extends SearchableLimitableFacet<F, S>, NameableLimitableFacet<F, S>, NameableSearchableFacet<F, S> {
}
