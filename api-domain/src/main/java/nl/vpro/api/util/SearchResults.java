/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.api.util;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.DurationRangeMatcher;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.classification.Term;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
@Slf4j
public class SearchResults {

    private SearchResults() {
    }

    public static <M extends Serializable> List<M> unwrap(final SearchResult<M> wrapped) {
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

    private static final ConcurrentMap<String, Map<String, Optional<String>>> VALUE_CACHES = new ConcurrentHashMap<>();


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
        Map<String, Optional<String>> valueCache = VALUE_CACHES.computeIfAbsent(valueCacheName, (k) -> new ConcurrentHashMap<>());

        if (facetResultItems != null && searches != null && searches.getMatch() == Match.MUST) {
            for (T facetResultItem : facetResultItems) {
                String id = facetResultItem.getId();
                if (facetResultItem.getValue() != null && !Objects.equals(id, facetResultItem.getValue())) {
                    if (!valueCache.containsKey(id)) {
                        valueCache.put(id, Optional.ofNullable(facetResultItem.getValue()));
                        log.info("value for {}: {} -> {}", valueCacheName, id, facetResultItem.getValue());
                    }
                }
                facetResultItem.setSelected(contains(searches, id));
                if (facetResultItem.isSelected()) {
                    selected.add(facetResultItem);
                }
            }
            for (AbstractTextMatcher<?> item : searches) {
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
                            Optional<String> value = valueCache.computeIfAbsent(item.getValue(), valueCreator);
                            newItem.setValue(value.orElse(null));
                            newItem.setCount(0);
                            newItem.setSelected(true);
                            selected.add(newItem);
                        } catch (Exception ex) {
                            log.debug(ex.getMessage());
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
            log.warn(e.getClass() + " " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void setSelectedDateFacets(DateRangeMatcherList searches, DateRangeFacets<?> dateRangeFacets, List<DateFacetResultItem> facetResultItems, List<DateFacetResultItem> selected, Callable<DateFacetResultItem> creator) {

        if (facetResultItems != null && searches != null) {
            for (DateFacetResultItem facetResultItem : facetResultItems) {
                Instant begin = facetResultItem.getBegin();
                Instant end = facetResultItem.getEnd();
                for (DateRangeMatcher matcher : searches) {
                    Instant matcherBegin = matcher.getBegin();
                    Instant matcherEnd = matcher.getEnd();
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
                                for (RangeFacet<Instant> range : dateRangeFacets.getRanges()) {
                                    final DateRangeFacetItem dateRangeFacetItem;
                                    if (range instanceof DateRangePreset) {
                                        dateRangeFacetItem = ((DateRangePreset) range).asDateRangeFacetItem();
                                    } else if (range instanceof DateRangeFacetItem) {
                                        dateRangeFacetItem = (DateRangeFacetItem) range;
                                    } else {
                                        DateRangeInterval dateRangeInterval = (DateRangeInterval) range;
                                        if (dateRangeInterval.matches(item.getBegin(), item.getEnd())) {
                                            newItem.setValue(dateRangeInterval.getInterval().print(item.getBegin()));
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
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    public static void setSelectedDurationsFacets(DurationRangeMatcherList searches, DurationRangeFacets<?> durationRangeFacets, List<DurationFacetResultItem> facetResultItems, List<DurationFacetResultItem> selected, Callable<DurationFacetResultItem> creator) {

        if (facetResultItems != null && searches != null) {
            for (DurationFacetResultItem facetResultItem : facetResultItems) {
                Duration begin = facetResultItem.getBegin();
                Duration end = facetResultItem.getEnd();
                for (DurationRangeMatcher matcher : searches) {
                    Duration matcherBegin = matcher.getBegin();
                    Duration matcherEnd = matcher.getEnd();
                    if (Objects.equals(matcherBegin, begin) &&
                        Objects.equals(matcherEnd, end)) {
                        facetResultItem.setSelected(true);
                        selected.add(facetResultItem);
                    }
                }

            }
            for (DurationRangeMatcher item : searches) {
                if (item.getMatch() != Match.NOT) {
                    boolean found = false;
                    for (DurationFacetResultItem selectItem : selected) {
                        if (Objects.equals(selectItem.getBegin(), item.getBegin()) && Objects.equals(selectItem.getEnd(), item.getEnd())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        try {
                            DurationFacetResultItem newItem = creator.call();
                            newItem.setBegin(item.getBegin());
                            newItem.setEnd(item.getEnd());
                            newItem.setCount(0);
                            newItem.setSelected(true);
                            if (durationRangeFacets != null && durationRangeFacets.getRanges() != null) {
                                for (RangeFacet<Duration> range : durationRangeFacets.getRanges()) {
                                    final DurationRangeFacetItem durationRangeFacetItem;
                                    if (range instanceof DurationRangeFacetItem) {
                                        durationRangeFacetItem = (DurationRangeFacetItem) range;
                                    } else {
                                        DurationRangeInterval dateRangeInterval = (DurationRangeInterval) range;
                                        if (dateRangeInterval.matches(item.getBegin(), item.getEnd())) {
                                            newItem.setValue(dateRangeInterval.getInterval().print(item.getBegin()));
                                            selected.add(newItem);
                                        }
                                        continue;
                                    }
                                    newItem.setValue(durationRangeFacetItem.getName());
                                    if (durationRangeFacetItem.getBegin().equals(item.getBegin()) && durationRangeFacetItem.getEnd().equals(item.getEnd())) {
                                        newItem.setValue(durationRangeFacetItem.getName());
                                        selected.add(newItem);
                                        break;
                                    }
                                }
                            } else {
                                selected.add(newItem);
                            }

                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }


    public static void setSelectedRelations(NameableSearchableFacet<?, ? extends AbstractRelationSearch>  facet, Iterable<? extends AbstractRelationSearch> searches, List<MultipleFacetsResult> facetResultItems, List<MultipleFacetsResult> selected) {

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
                                        break;
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


    public static boolean contains(Iterable<? extends AbstractTextMatcher<?>> matchers, String value) {
        return contains(matchers, value, input -> {
            if (input == null) {
                return null;
            }
            return input.getValue();
        });
    }

    private static boolean contains(Iterable<? extends AbstractTextMatcher<?>> matchers, String value, Function<AbstractTextMatcher<?>, String> function) {
        if (matchers == null) {
            return false;
        }
        for (AbstractTextMatcher<?> matcher : matchers) {
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

    public static void sort(TextFacet<?, ?>  facet, List<? extends TermFacetResultItem> results, List<? extends TermFacetResultItem> selected) {
        if (facet == null) return;
        FacetOrder sort = facet.getSort();
        if (sort == null) return;
        if (results != null) {
            results.sort(FacetOrder.toComparator(sort));
        }
        if (selected != null) {
            selected.sort(FacetOrder.toComparator(sort));
        }
    }

    public static void sortWithCustomComparator(TextFacet<?, ?> facet, List<? extends TermFacetResultItem> results, List<? extends TermFacetResultItem> selected, Comparator<TermFacetResultItem> comparator) {
        if (facet != null) {
            if (results != null) {
                results.sort(comparator);
            }
            if (selected != null) {
                selected.sort(comparator);
            }
        }
    }
}
