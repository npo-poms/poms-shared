package nl.vpro.domain.api;


import java.util.AbstractList;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */

@XmlType(name = "searchResultType")
@XmlSeeAlso({SearchResultItem.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult<S> extends Result<SearchResultItem<? extends S>> {

    public SearchResult() {
    }

    public SearchResult(List<SearchResultItem<? extends S>> list, Long offset, Integer max, Long total, TotalQualifier totalQualifier) {
        super(list, offset, max, total, totalQualifier);
    }

    public SearchResult(SearchResult<? extends S> copy) {
        super(copy);
    }

    /**
     * Returns a view on this SearchResult as a list of unwrapped objects (so not wrapped by {@link SearchResultItem}s
     */
    public List<S> asList() {
        return new AbstractList<S>() {
            @Override
            public S get(int index) {
                SearchResultItem<? extends S> item = SearchResult.this.getItems().get(index);
                return item == null ? null : item.getResult();
            }

            @Override
            public int size() {
                return SearchResult.this.getSize();
            }
        };
    }

    /**
     * Returns this SearchResult as a {@link #Result}, which means that all items are unwrapped, and the facet results are removed.
     */
    public Result<S> asResult() {
        return new Result<>(asList(), getOffset(), getMax(), getTotal(), getTotalQualifier());
    }
}
