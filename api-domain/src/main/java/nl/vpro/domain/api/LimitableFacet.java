package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface LimitableFacet<F extends AbstractSearch> extends Facet<F> {

    Integer getThreshold();

    Integer getMax();

    String getInclude();

    String getScript();

    FacetOrder getSort();

}
