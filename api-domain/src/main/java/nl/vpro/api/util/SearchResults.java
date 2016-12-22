/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.util;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.api.*;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.classification.Term;
import nl.vpro.util.DateUtils;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
public class SearchResults {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResults.class);


    public static <M> List<M> unwrap(final SearchResult<M> wrapped) {
        return new AbstractList<M>() {
            @Override
            public M get(int index) {
                return wrapped.getItems().get(index).getResult();
            }

            @Override
            public int size() {
                return wrapped.getItems().size();
            }
        };
    }

    private static Map<String, Map<String, Optional<String>>> valueCaches = new ConcurrentHashMap<>();


    public static <T extends TermFacetResultItem, S extends MatchType> void setSelected(
        AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S> searches,
        List<T> facetResultItems,
        List<T> selected,
        Callable<T> creator,
        String valueCacheName
    ) {
        setSelected(searches, facetResultItems, selected, creator, valueCacheName, Optional::of);
    }


    public static <T extends TermFacetResultItem, S extends MatchType> void setSelected(
        AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S> searches,
        List<T> facetResultItems,
        List<T> selected,
        Callable<T> creator,
        String valueCacheName,
        Function<String, Optional<String>> valueCreator
    ) {
        if (! valueCaches.containsKey(valueCacheName)) {
            valueCaches.put(valueCacheName, new ConcurrentHashMap<>());
        }
        Map<String, Optional<String>> valueCache = valueCaches.get(valueCacheName);

        if (facetResultItems != null && searches != null && searches.getMatch() == Match.MUST) {
            for (T facetResultItem : facetResultItems) {
                String id = facetResultItem.getId();
                if (facetResultItem.getValue() != null && !Objects.equals(id, facetResultItem.getValue())) {
                    if (!valueCache.containsKey(id)) {
                        valueCache.put(id, Optional.ofNullable(facetResultItem.getValue()));
                        LOG.info("{}: {} -> {}", valueCacheName, id, facetResultItem.getValue());
                    }
                }
                facetResultItem.setSelected(contains(searches, id));
                if (facetResultItem.isSelected()) {
                    selected.add(facetResultItem);
                }
            }
            for (AbstractTextMatcher item : searches) {
                if (item.getMatch() != Match.NOT) {
                    boolean find = false;
                    for (TermFacetResultItem selectItem : selected) {
                        if (selectItem.getId().equals(item.getValue())) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {
                        try {
                            T newItem = creator.call();
                            newItem.setId(item.getValue());
                            Optional<String> value = valueCache.get(item.getValue());
                            if (value == null) {
                                value = valueCreator.apply(item.getValue());
                                valueCache.put(item.getValue(), value);
                            }
                            newItem.setValue(value.orElse(null));
                            newItem.setCount(0);
                            newItem.setSelected(true);
                            selected.add(newItem);
                        } catch (Exception ex) {
                            /* Ignore */
                        }
                    }
                }
            }
        }
    }

    public static Optional<String> getGenreValue(String id) {
        try {
            Term term = ClassificationServiceLocator.getInstance().getTerm(id);
            if (term == null) {
                return Optional.empty();
            } else {
                return Optional.ofNullable(term.getName());
            }
        } catch (Exception e) {
            LOG.warn(e.getClass() + " " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void setSelected(DateRangeMatcherList searches, DateRangeFacets<?> dateRangeFacets,  List<DateFacetResultItem> facetResultItems, List<DateFacetResultItem> selected, Callable<DateFacetResultItem> creator, boolean asDuration) {

        if (facetResultItems != null && searches != null) {
            for (DateFacetResultItem facetResultItem : facetResultItems) {
                Date begin = facetResultItem.getBegin();
                Date end = facetResultItem.getEnd();
                for (DateRangeMatcher matcher : searches) {
                    Date matcherBegin = matcher.getBegin();
                    Date matcherEnd = matcher.getEnd();
                    if (Objects.equals(matcherBegin, begin)  &&
                        Objects.equals(matcherEnd, end)) {
                        facetResultItem.setSelected(true);
                        selected.add(facetResultItem);
                    }
                }

            }
            for (DateRangeMatcher item : searches) {
                if (item.getMatch() != Match.NOT) {
                    boolean found = false;
                    for (DateFacetResultItem selectItem : selected) {
                        if (Objects.equals(selectItem.getBegin(), item.getBegin()) && Objects.equals(selectItem.getEnd(), item.getEnd())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        try {
                            DateFacetResultItem newItem = creator.call();
                            newItem.setBegin(item.getBegin());
                            newItem.setEnd(item.getEnd());
                            newItem.setCount(0);
                            newItem.setSelected(true);
                            if (dateRangeFacets != null && dateRangeFacets.getRanges() != null) {
                                for (RangeFacet<Date> range : dateRangeFacets.getRanges()) {
                                    final DateRangeFacetItem dateRangeFacetItem;
                                    if (range instanceof DateRangePreset) {
                                        dateRangeFacetItem = ((DateRangePreset) range).asDateRangeFacetItem();
                                    } else if (range instanceof DateRangeFacetItem) {
                                        dateRangeFacetItem = (DateRangeFacetItem) range;
                                    } else {
                                        DateRangeInterval dateRangeInterval = (DateRangeInterval) range;
                                        if (dateRangeInterval.matches(item.getBegin(), item.getEnd())) {
                                            newItem.setValue(dateRangeInterval.parsed().print(DateUtils.toInstant(item.getBegin()), asDuration));
                                            selected.add(newItem);
                                        }
                                        continue;
                                    }
                                    if (dateRangeFacetItem.getBegin().equals(item.getBegin()) && dateRangeFacetItem.getEnd().equals(item.getEnd())) {
                                        newItem.setValue(dateRangeFacetItem.getName());
                                        selected.add(newItem);
                                        break;
                                    }
                                }
                            } else {
                                selected.add(newItem);
                            }

                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }


    public static void setSelected(NameableSearchableFacet<? extends AbstractRelationSearch> facet, Iterable<? extends AbstractRelationSearch> searches, List<MultipleFacetsResult> facetResultItems, List<MultipleFacetsResult> selected) {

        // A relation is selected if the search applies to the subsearch and the search.
        for (MultipleFacetsResult multipleFacetsResult : facetResultItems) {
            if (facet.getName().equals(multipleFacetsResult.getName())) {
                MultipleFacetsResult selectedMultiple = new MultipleFacetsResult();
                selectedMultiple.setName(multipleFacetsResult.getName());
                selectedMultiple.setFacets(new ArrayList<>());
                selected.add(selectedMultiple);
                for (TermFacetResultItem facetResultItem : multipleFacetsResult) {
                    String id = facetResultItem.getId();
                    for (AbstractRelationSearch relationSearch : searches) {
                        if (relationSearch.searchEqualsOrNarrows(facet.getSubSearch())) {
                            if (SearchResults.contains(relationSearch.getValues(), id)) {
                                facetResultItem.setSelected(true);
                                selectedMultiple.getFacets().add(facetResultItem);
                            }
                        }
                    }
                }
                for (AbstractRelationSearch relationSearch : searches) {
                    if (relationSearch.searchEqualsOrNarrows(facet.getSubSearch())) {

                        if (relationSearch.getValues() != null) {
                            for (ExtendedTextMatcher term : relationSearch.getValues()) {
                                boolean find = false;
                                if (! term.isCaseSensitive()) {
                                    term = term.toLowerCase();
                                }
                                for (TermFacetResultItem selectedItem : selectedMultiple) {
                                    if (Objects.equals(selectedItem.getId(), term.getValue())) {
                                        find = true;
                                    }
                                }
                                if (!find) {
                                    TermFacetResultItem newItem = new TermFacetResultItem();
                                    newItem.setSelected(true);
                                    newItem.setCount(0);
                                    newItem.setId(term.getValue());
                                    selectedMultiple.getFacets().add(newItem);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static boolean contains(Iterable<? extends AbstractTextMatcher> matchers, String value) {
        return contains(matchers, value, input -> {
            if (input == null) {
                return null;
            }
            return input.getValue();
        });
    }

    private static boolean contains(Iterable<? extends AbstractTextMatcher> matchers, String value, Function<AbstractTextMatcher, String> function) {
        if (matchers == null) {
            return false;
        }
        for (AbstractTextMatcher matcher : matchers) {
            if (matcher.isCaseSensitive()) {
                if (function.apply(matcher).equals(value)) {
                    return true;
                }
            } else {
                if (function.apply(matcher).equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void sort(TextFacet<?>  facet, List<? extends TermFacetResultItem> results, List<? extends TermFacetResultItem> selected) {
        if (facet == null) return;
        FacetOrder sort = facet.getSort();
        if (sort == null) return;
        if (results != null) {
            Collections.sort(results, FacetOrder.toComparator(sort));
        }
        if (selected != null) {
            Collections.sort(selected, FacetOrder.toComparator(sort));
        }
    }

    public static void sortWithCustomComparator(TextFacet<?> facet, List<? extends TermFacetResultItem> results, List<? extends TermFacetResultItem> selected, Comparator<TermFacetResultItem> comparator) {
        if (facet != null) {
            if (results != null) {
                Collections.sort(results, comparator);
            }
            if (selected != null) {
                Collections.sort(selected, comparator);
            }
        }
    }
}
