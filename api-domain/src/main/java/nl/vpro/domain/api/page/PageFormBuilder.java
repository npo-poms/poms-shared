package nl.vpro.domain.api.page;

import java.time.Instant;
import java.util.*;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.media.MediaFacets;
import nl.vpro.domain.api.media.MediaForm;
import nl.vpro.domain.api.media.MediaFormBuilder;
import nl.vpro.domain.page.PageType;
import nl.vpro.domain.page.RelationDefinition;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageFormBuilder extends AbstractFormBuilder {

    private final PageForm form = new PageForm();

    private MediaFormBuilder mediaFormBuilder;

    private PageFormBuilder() {
    }

    public static PageFormBuilder form() {
        return new PageFormBuilder();
    }

    public PageForm build() {
        return form;
    }

    public PageFormBuilder text(String text) {
        return text(Match.SHOULD, text);
    }

    public PageFormBuilder text(Match match, String text) {
        return text(simpleTextMatcher(text, match));
    }

    public PageFormBuilder text(SimpleTextMatcher textMatcher) {
        search().setText(textMatcher);
        return this;
    }

    public PageFormBuilder broadcasters(String... broadcasters) {
        search().setBroadcasters(textMatchers(Match.MUST, broadcasters));
        return this;
    }


    public PageFormBuilder types(PageType... types) {
        search().setTypes(textMatchers(types));
        return this;
    }

    public PageFormBuilder addTypes(PageType... types) {
        TextMatcherList matchers = search().getTypes();
        if (matchers == null) {
            types(types);
        } else {
            matchers.asList().addAll(textMatchers(types).asList());
        }
        return this;
    }

    public PageFormBuilder sortDate(Instant from, Instant to) {
        return sortDate(from, to, false);
    }

    public PageFormBuilder sortDate(Instant  from, Instant to, Boolean inclusiveEnd) {
        search().setSortDates(dateRange(search().getSortDates(), from, to, inclusiveEnd));
        return this;
    }

    public PageFormBuilder creationDate(Instant from, Instant to) {
        return creationDate(from, to, false);
    }

    public PageFormBuilder creationDate(Instant from, Instant to, Boolean inclusiveEnd) {
        search().setCreationDates(dateRange(search().getCreationDates(), from, to, inclusiveEnd));
        return this;
    }

    public PageFormBuilder lastModified(Instant from, Instant to) {
        return lastModified(from, to, false);
    }

    public PageFormBuilder lastModified(Instant from, Instant to, Boolean inclusiveEnd) {
        search().setLastModifiedDates(dateRange(search().getLastModifiedDates(), from, to, inclusiveEnd));
        return this;
    }

    public PageFormBuilder lastPublished(Instant from, Instant to) {
        return lastPublished(from, to, false);
    }

    public PageFormBuilder lastPublished(Instant from, Instant to, Boolean inclusiveEnd) {
        search().setPublishDates(dateRange(search().getPublishDates(), from, to, inclusiveEnd));
        return this;
    }
    protected DateRangeMatcherList dateRange(DateRangeMatcherList list, Instant from, Instant to, Boolean inclusiveEnd) {
        if (list == null) {
            list = new DateRangeMatcherList();
        }
        list.asList().add(new DateRangeMatcher(from, to, inclusiveEnd));
        return list;
    }

    public PageFormBuilder portals(String... portalUrls) {
        search().setPortals(textMatchers(Match.MUST, portalUrls));
        return this;
    }


    public PageFormBuilder addPortals(String... portals) {
        TextMatcherList textMatcherList = search().getPortals();
        if (textMatcherList == null) {
            textMatcherList = new TextMatcherList();
            search().setPortals(textMatcherList);
        }
        List<TextMatcher> matchers = textMatcherList.asList();
        if (matchers == null) {
            matchers = new ArrayList<>();
        }
        matchers.addAll(textMatchers(Match.MUST, portals).asList());
        return this;
    }

    public PageFormBuilder sections(String... sectionIds) {
        search().setSections(textMatchers(Match.MUST, sectionIds));
        return this;
    }


    public PageFormBuilder addSections(String... ids) {
        TextMatcherList textMatcherList = search().getSections();
        if (textMatcherList == null) {
            textMatcherList = new TextMatcherList();
            search().setSections(textMatcherList);
        }
        List<TextMatcher> matchers = textMatcherList.asList();
        if (matchers == null) {
            matchers = new ArrayList<>();
        }
        matchers.addAll(textMatchers(Match.MUST, ids).asList());
        return this;
    }

    public PageFormBuilder genres(String... genres) {
        search().setGenres(textMatchers(Match.MUST, genres));
        return this;
    }

    public PageFormBuilder tags(String... tags) {
        search().setTags(extendedTextMatchers(Match.MUST, tags));
        return this;
    }

    public PageFormBuilder keywords(String... keywords) {
        search().setKeywords(extendedTextMatchers(Match.MUST, keywords));
        return this;
    }

    public PageFormBuilder keywords(StandardMatchType matchType, String... keywords) {
        return keywords(matchType, false, keywords);
    }

    public PageFormBuilder keywords(StandardMatchType matchType, boolean caseSensitive, String... keywords) {
        search().setKeywords(extendedTextMatchers(Match.MUST, matchType, caseSensitive, keywords));
        return this;
    }


    public PageFormBuilder addGenres(String... genres) {
        TextMatcherList list = search().getGenres();
        if (list != null) {
            addTextMatchers(list, genres);
        } else {
            genres(genres);
        }
        return this;
    }

    public PageFormBuilder relation(RelationDefinition definition, String text, String uri) {
        return relation(definition, ExtendedTextMatcher.must(text), TextMatcher.must(uri));
    }
    public PageFormBuilder relation(RelationDefinition definition, ExtendedTextMatcher text, TextMatcher uri) {
        RelationSearch relationSearch = new RelationSearch();
        RelationSearchList search = search().getRelations();
        if (text != null) {
            relationSearch.setValues(ExtendedTextMatcherList.must(text));
        }
        if (uri != null) {
            relationSearch.setUriRefs(TextMatcherList.must(uri));
        }
        relationSearch.setBroadcasters(TextMatcherList.must(TextMatcher.must(definition.getBroadcaster())));
        relationSearch.setTypes(TextMatcherList.must(TextMatcher.must(definition.getType())));
        if (search == null) {
            search = new RelationSearchList();
            search().setRelations(search);
        }
        search.asList().add(relationSearch);

        return this;
    }

    public PageFormBuilder relationText(RelationDefinition definition, ExtendedTextMatcher text) {
        return relation(definition, text, null);
    }

    public PageFormBuilder relationUri(RelationDefinition definition, String uri) {
        return relation(definition, null, uri);
    }

    public PageFormBuilder relationText(RelationDefinition definition, String text) {
        return relation(definition, text, null);
    }

    public PageFormBuilder relationUri(RelationDefinition definition, TextMatcher uri) {
        return relation(definition, null, uri);
    }

    public PageFormBuilder relationsFacet() {
        return relationsFacet(true);
    }


    public PageFormBuilder relationsFacet(boolean casesensitive) {
        RelationFacet rs = new RelationFacet();
        rs.setCaseSensitive(casesensitive);
        return relationsFacet(rs);
    }

    public PageFormBuilder relationsFacet(RelationFacet... facet) {
        facets().setRelations(new RelationFacetList(Arrays.asList(facet)));
        return this;
    }

    public PageFormBuilder relationsFacet(RelationFacetList facets) {
        facets().setRelations(facets);
        return this;
    }

    public PageFormBuilder referrals(AssociationSearch... associationSearches) {
        AssociationSearchList list = search().getReferrals();
        if (list == null) {
            list = new AssociationSearchList();
            search().setReferrals(list);
        }
        list.asList().addAll(Arrays.asList(associationSearches));
        return this;
    }


    public PageFormBuilder links(AssociationSearch... associationSearches) {
        AssociationSearchList list = search().getLinks();
        if (list == null) {
            list = new AssociationSearchList();
            search().setLinks(list);
        }
        list.asList().addAll(Arrays.asList(associationSearches));
        return this;
    }

    public PageFormBuilder highlight(boolean b) {
        form.setHighlight(b);
        return this;
    }

    @SafeVarargs
    public final PageFormBuilder sortDateFacet(RangeFacet<Instant>... ranges) {
        DateRangeFacets dateRangeFacets = new DateRangeFacets();
        dateRangeFacets.setRanges(Arrays.asList(ranges));
        facets().setSortDates(dateRangeFacets);
        return this;
    }

    public PageFormBuilder broadcasterFacet() {
        facets().setBroadcasters(countPageFacet());
        return this;
    }

    public PageFormBuilder broadcasterFacet(int threshold) {
        facets().setBroadcasters(countPageFacet(threshold));
        return this;
    }

    public PageFormBuilder typeFacet() {
        facets().setTypes(countPageFacet());
        return this;
    }


    public PageFormBuilder typeFacet(Integer treshhold) {
        facets().setTypes(countPageFacet(treshhold));
        return this;
    }

    public PageFormBuilder genreFacet() {
        facets().setGenres(countSearchableTermFacet());
        return this;
    }

    public PageFormBuilder genreFacet(PageSearchableTermFacet facet) {
        facets().setGenres(facet);
        return this;
    }

    public PageFormBuilder portalFacet() {
        facets().setPortals(countPageFacet());
        return this;
    }

    public PageFormBuilder sectionFacet() {
        facets().setSections(countPageFacet());
        return this;
    }

    public PageFormBuilder tagFacet() {
        return tagFacet(true);
    }

    public PageFormBuilder tagFacet(boolean caseSensitive) {
        facets().setTags(countExtendedPageFacet(caseSensitive));
        return this;
    }

    public PageFormBuilder keywordFacet() {
        return keywordFacet(true);
    }

    public PageFormBuilder keywordFacet(boolean caseSensitive) {
        facets().setKeywords(countExtendedPageFacet(caseSensitive));
        return this;
    }

    public PageFormBuilder addSortField(String field, Order order) {
        return addSortField(PageSortField.valueOf(field), order);
    }

    public PageFormBuilder addSortField(PageSortField field, Order order) {
        form.addSortField(field, order);
        return this;
    }

    public PageFormBuilder asc(PageSortField field) {
        return addSortField(field, Order.ASC);
    }

    public PageFormBuilder desc(PageSortField field) {
        return addSortField(field, Order.DESC);
    }


    public PageFormBuilder mediaFacet(MediaFacets facet) {
        getMediaForm().setFacets(facet);
        return this;
    }

    public MediaFormBuilder mediaForm() {
        getMediaForm();
        return mediaFormBuilder;
    }

    private PageFacet countPageFacet() {
        return countPageFacet(null);
    }

    private PageFacet countPageFacet(Integer threshold) {
        PageFacet facet = new PageFacet();
        facet.setSort(FacetOrder.COUNT_DESC);
        facet.setThreshold(threshold);
        return facet;
    }

    private ExtendedPageFacet countExtendedPageFacet(boolean caseSensitive) {
        ExtendedPageFacet facet = new ExtendedPageFacet();
        facet.setSort(FacetOrder.COUNT_DESC);
        facet.setCaseSensitive(caseSensitive);
        return facet;
    }

    private PageSearchableTermFacet countSearchableTermFacet() {
        PageSearchableTermFacet facet = new PageSearchableTermFacet();
        facet.setSort(FacetOrder.COUNT_DESC);
        return facet;
    }

    private PageSearch search() {
        if(form.getSearches() == null) {
            form.setSearches(new PageSearch());
        }
        return form.getSearches();
    }

    private TextMatcherList textMatchers(PageType[] types) {
        List<TextMatcher> result = new ArrayList<>();
        for(PageType type : types) {
            result.add(new TextMatcher(type.name(), Match.SHOULD));
        }
        return new TextMatcherList(result, Match.MUST);
    }

    private PageFacets facets() {
        if(form.getFacets() == null) {
            form.setFacets(new PageFacets());
        }
        return form.getFacets();
    }

    private MediaFacets mediaFacets() {
        if(getMediaForm().getFacets() == null) {
            getMediaForm().setFacets(new MediaFacets());
        }
        return form.getMediaForm().getFacets();
    }

    private MediaForm getMediaForm() {
        if(mediaFormBuilder == null) {
            mediaFormBuilder = MediaFormBuilder.form();
            form.setMediaForm(mediaFormBuilder.build());
        }
        return form.getMediaForm();
    }
}
