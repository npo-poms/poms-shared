package nl.vpro.domain.api;


import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */

@XmlType(name = "searchResultType")
@XmlSeeAlso({SearchResultItem.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult<S extends Serializable> extends Result<SearchResultItem<? extends S>> {


    public SearchResult() {
    }

    public SearchResult(List<SearchResultItem<? extends S>> list, Long offset, Integer max, Total total) {
        super(list, offset, max, total);
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
                return SearchResult.this.getResult(index);
            }

            @Override
            public int size() {
                return SearchResult.this.getSize();
            }
        };
    }

    /**
     * @since 5.12
     * @throws IndexOutOfBoundsException  if the index is out of range (index < 0 || index >= size())
     */
    public S getResult(int i) {
        SearchResultItem<? extends S>  item =  get(i);
        return item == null ? null : item.getResult();
    }

    /**
     * Returns this SearchResult as a {@link #Result}, which means that all items are unwrapped, and the facet results are removed.
     */
    public Result<S> asResult() {
        return new Result<>(asList(), getOffset(), getMax(), new Total(getTotal(), getTotalQualifier()));
    }
}
