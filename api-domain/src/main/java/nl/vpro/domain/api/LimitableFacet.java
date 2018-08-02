package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public interface LimitableFacet<T extends AbstractSearch> extends Facet<T> {

    Integer getThreshold();

    Integer getMax();

    String getInclude();

    String getScript();

    FacetOrder getSort();

}
