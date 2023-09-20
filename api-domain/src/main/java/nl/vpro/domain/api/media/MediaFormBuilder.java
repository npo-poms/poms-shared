/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.time.*;
import java.util.*;
import java.util.regex.Pattern;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class MediaFormBuilder extends AbstractFormBuilder {

    private final MediaForm form;

    private MediaFormBuilder(MediaForm form) {
        this.form = form;
    }

    public static MediaFormBuilder form() {
        return from(new MediaForm());
    }

    public static MediaFormBuilder from(MediaForm form) {
        if (form == null) {
            form = new MediaForm();
        }
        return new MediaFormBuilder(form);
    }

    public static MediaForm emptyForm() {
        return form().build();
    }

    public MediaForm build() {
        return form;
    }

    // if property can have more than one value, the default Match value is MUST, otherwise it is SHOULD

    public MediaFormBuilder text(String text) {
        search().setText(simpleTextMatcher(text, Match.SHOULD));
        return this;
    }

    public MediaFormBuilder fuzzyText(String text) {
        return fuzzyText(text, "AUTO");
    }

    private MediaFormBuilder fuzzyText(String text, String fuzziness) {
        SimpleTextMatcher matcher = simpleTextMatcher(text, Match.SHOULD);
        matcher.setFuzziness(fuzziness);
        search().setText(matcher);
        return this;
    }

    public MediaFormBuilder mediaIds(String... mediaIds) {
        search().setMediaIds(textMatchers(Match.SHOULD, mediaIds));
        return this;
    }

    public MediaFormBuilder mediaIds(Match match, String... mediaIds) {
        search().setMediaIds(textMatchers(match, mediaIds));
        return this;
    }

    public MediaFormBuilder sortDate(Instant begin, Instant end) {
        return sortDate(begin, end, false);
    }

    public MediaFormBuilder sortDate(Instant begin, Instant end, boolean inclusiveEnd) {
        DateRangeMatcherList list = search().getSortDates();
        if (list == null) {
            list = new DateRangeMatcherList();
            search().setSortDates(list);
        }
        list.asList().add(new DateRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }


    public MediaFormBuilder publishDate(Instant begin, Instant end) {
        return publishDate(begin, end, false);
    }


    public MediaFormBuilder publishDate(Instant begin, Instant end, boolean inclusiveEnd) {
        DateRangeMatcherList list = search().getPublishDates();
        if (list == null) {
            list = new DateRangeMatcherList();
            search().setPublishDates(list);
        }
        list.asList().add(new DateRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }

    public MediaFormBuilder broadcasters(String... broadcasters) {
        broadcasters(Match.SHOULD, broadcasters);
        return this;
    }

    public MediaFormBuilder broadcasters(Match match, String... broadcasters) {
        search().setBroadcasters(textMatchers(match, broadcasters));
        return this;
    }

    public MediaFormBuilder broadcasters(Match match, TextMatcher... matchers) {
        search().setBroadcasters(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder locations(String... locations) {
        search().setLocations(textMatchers(Match.SHOULD, locations));
        return this;
    }

    public MediaFormBuilder tags(String... tags) {
        search().setTags(extendedTextMatchers(Match.SHOULD, tags));
        return this;
    }

    public MediaFormBuilder tags(boolean caseSensitive, String... tags) {
        search().setTags(extendedTextMatchers(Match.SHOULD, tags));
        return this;
    }


    public MediaFormBuilder tags(Tag... tags) {
        search().setTags(extendedTextMatchers(Match.SHOULD, tags));
        return this;
    }

    public MediaFormBuilder tags(Match match, Tag... tags) {
        search().setTags(extendedTextMatchers(match, tags));
        return this;
    }

    public MediaFormBuilder tags(Match match, ExtendedTextMatcher... matchers) {
        search().setTags(extendedTextMatchers(match, matchers));
        return this;
    }


    public MediaFormBuilder tags(ExtendedTextMatcher... matchers) {
        return tags(Match.SHOULD, matchers);
    }


    public MediaFormBuilder genres(String... terms) {
        return genres(Match.SHOULD, terms);
    }

    public MediaFormBuilder genres(Match match, String... terms) {
        search().setGenres(textMatchers(match, terms));
        return this;
    }

    public MediaFormBuilder genres(Match match, TextMatcher... matchers) {
        search().setGenres(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder types(MediaType... types) {
        search().setTypes(textMatchers(Match.SHOULD, types));
        return this;
    }

    public MediaFormBuilder types(Match match, MediaType... types) {
        search().setTypes(textMatchers(match, types));
        return this;
    }

    public MediaFormBuilder types(Match match, TextMatcher... matchers) {
        search().setTypes(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder avTypes(AVType... avTypes) {
        search().setAvTypes(textMatchers(Match.SHOULD, avTypes));
        return this;
    }


    public MediaFormBuilder avTypes(Match match, AVType... avTypes) {
        search().setAvTypes(textMatchers(match, avTypes));
        return this;
    }

    public MediaFormBuilder avTypes(Match match, TextMatcher... matchers) {
        search().setAvTypes(textMatchers(match, matchers));
        return this;
    }


    public MediaFormBuilder duration(Duration begin, Duration end) {
        return duration(begin, end, false);
    }

    public MediaFormBuilder duration(Duration begin, Duration end, boolean inclusiveEnd) {
        DurationRangeMatcherList list = search().getDurations();
        if (list == null) {
            list = new DurationRangeMatcherList();
            search().setDurations(list);
        }
        list.asList().add(new DurationRangeMatcher(begin, end, inclusiveEnd));
        return this;
    }


    public MediaFormBuilder episodeOfs(String... mids) {
        search().setEpisodeOf(textMatchers(Match.SHOULD, mids));
        return this;
    }

    public MediaFormBuilder episodeOfs(Match match, String... mids) {
        search().setEpisodeOf(textMatchers(match, mids));
        return this;
    }

    public MediaFormBuilder episodeOfs(Match match, TextMatcher... matchers) {
        search().setEpisodeOf(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder descendantOfs(String... mids) {
        search().setDescendantOf(textMatchers(Match.SHOULD, mids));
        return this;
    }

    public MediaFormBuilder descendantOfs(Match match, String... mids) {
        search().setDescendantOf(textMatchers(match, mids));
        return this;
    }

    public MediaFormBuilder descendantOfs(Match match, TextMatcher... matchers) {
        search().setDescendantOf(textMatchers(match, matchers));
        return this;
    }

    public MediaFormBuilder memberOfs(String... mids) {
        search().setMemberOf(textMatchers(Match.SHOULD, mids));
        return this;
    }

    public MediaFormBuilder memberOfs(Match match, String... mids) {
        search().setMemberOf(textMatchers(match, mids));
        return this;
    }


    public MediaFormBuilder memberOfs(Match match, TextMatcher... matchers) {
        search().setMemberOf(textMatchers(match, matchers));
        return this;
    }


    public MediaFormBuilder ageRating(AgeRating... ageRatings) {
        search().setAgeRatings(textMatchers(Match.SHOULD, ageRatings));
        return this;
    }

    public MediaFormBuilder ageRating(Pattern ageRatings) {
        TextMatcherList list = new TextMatcherList();
        list.getMatchers().add(new TextMatcher(ageRatings.toString(), Match.MUST, StandardMatchType.REGEX));
        search().setAgeRatings(list);
        return this;
    }

    public MediaFormBuilder contentRatings(ContentRating... contentRatings) {
        return contentRatings(Match.SHOULD, contentRatings);
    }

    public MediaFormBuilder contentRatings(Match match, ContentRating... contentRatings) {
        search().setContentRatings(textMatchers(match, contentRatings));
        return this;
    }

    public MediaFormBuilder scheduleEvents(ScheduleEventSearch... scheduleEventSearch) {
        search().setScheduleEvents(Arrays.asList(scheduleEventSearch));
        return this;
    }

    public MediaFormBuilder titles(TitleSearch... titleSearch) {
        search().setTitles(Arrays.asList(titleSearch));
        return this;
    }


    public MediaFormBuilder relationText(RelationDefinition definition, ExtendedTextMatcher text) {
        return relation(definition, text, null);
    }

    public MediaFormBuilder relationUri(RelationDefinition definition, String uri) {
        return relation(definition, null, uri);
    }

    public MediaFormBuilder relationText(RelationDefinition definition, String text) {
        return relation(definition, text, null);
    }


    public MediaFormBuilder relation(RelationDefinition definition, String text, String uri) {
        return relation(definition, ExtendedTextMatcher.must(text), TextMatcher.must(uri));
    }

    public MediaFormBuilder relation(RelationDefinition definition, ExtendedTextMatcher text, TextMatcher uri) {
        RelationSearch relationSearch = new RelationSearch();
        RelationSearchList releationSearchList = search().getRelations();
        if (text != null) {
            relationSearch.setValues(ExtendedTextMatcherList.must(text));
        }
        if (uri != null) {
            relationSearch.setUriRefs(TextMatcherList.must(uri));
        }
        relationSearch.setBroadcasters(TextMatcherList.must(TextMatcher.must(definition.getBroadcaster())));
        relationSearch.setTypes(TextMatcherList.must(TextMatcher.must(definition.getType())));
        if (releationSearchList == null) {
            releationSearchList = new RelationSearchList();
            search().setRelations(releationSearchList);
        }
        releationSearchList.asList().add(relationSearch);

        return this;
    }

    /**
     * @since 5.11
     */
    public MediaFormBuilder geoLocation(GeoLocationSearch... searches) {
        List<GeoLocationSearch> geoLocations = search().getGeoLocations();
        if (geoLocations == null) {
            geoLocations = new ArrayList<>();
            search().setGeoLocations(geoLocations);
        }
        geoLocations.addAll(Arrays.asList(searches));
        return this;
    }

    public MediaFormBuilder highlight(boolean b) {
        form.setHighlight(b);
        return this;
    }

    public MediaFormBuilder broadcasterFacet() {
        facets().setBroadcasters(new MediaFacet(0, FacetOrder.VALUE_ASC, 24));
        return this;
    }

    public MediaFormBuilder broadcasterFacet(MediaFacet facet) {
        facets().setBroadcasters(facet);
        return this;
    }

    public MediaFormBuilder genreFacet() {
        facets().setGenres(new MediaSearchableTermFacet());
        return this;
    }

    public MediaFormBuilder tagFacet() {
        facets().setTags(new ExtendedMediaFacet());
        return this;
    }

    public MediaFormBuilder tagFacet(boolean caseSensitive) {
        ExtendedMediaFacet emf = new ExtendedMediaFacet();
        emf.setCaseSensitive(caseSensitive);
        facets().setTags(emf);
        return this;
    }


    public MediaFormBuilder typeFacet() {
        facets().setTypes(new MediaFacet());
        return this;
    }

    public MediaFormBuilder avTypeFacet() {
        return avTypeFacet(new MediaFacet());
    }

    public MediaFormBuilder avTypeFacet(MediaFacet facet) {
        facets().setAvTypes(facet);
        return this;
    }

    @SafeVarargs
    public final MediaFormBuilder sortDateFacet(RangeFacet<Instant>... ranges) {
        DateRangeFacets<?> dateRangeFacets = new DateRangeFacets<>();
        dateRangeFacets.setRanges(Arrays.asList(ranges));
        facets().setSortDates(dateRangeFacets);
        return this;
    }

    @SafeVarargs
    public final MediaFormBuilder durationFacet(RangeFacet<Duration>... ranges) {
        DurationRangeFacets<?> dateRangeFacets = new DurationRangeFacets<>();
        dateRangeFacets.setRanges(Arrays.asList(ranges));
        facets().setDurations(dateRangeFacets);
        return this;
    }

    public MediaFormBuilder episodeOfFacet() {
        facets().setEpisodeOf(new MemberRefFacet());
        return this;
    }

    public MediaFormBuilder episodeOfFacet(MemberRefFacet facet) {
        facets().setEpisodeOf(facet);
        return this;
    }

    public MediaFormBuilder memberOfFacet() {
        facets().setMemberOf(new MemberRefFacet());
        return this;
    }

    public MediaFormBuilder memberOfFacet(MemberRefFacet facet) {
        facets().setMemberOf(facet);
        return this;
    }

    public MediaFormBuilder descendantOfFacet() {
        facets().setDescendantOf(new MemberRefFacet());
        return this;
    }

    public MediaFormBuilder descendantOfFacet(MemberRefFacet facet) {
        facets().setDescendantOf(facet);
        return this;
    }

    public MediaFormBuilder relationsFacet() {
        return relationsFacet(new RelationFacet());
    }

    public MediaFormBuilder relationsFacet(RelationFacet relationFacet) {
        facets().setRelations(new RelationFacetList(Collections.singletonList(relationFacet)));
        return this;
    }

    public MediaFormBuilder relationsFacet(String relationFacet) {
        RelationFacet rf = new RelationFacet();
        rf.setName(relationFacet);
        return relationsFacet(rf);
    }

    public MediaFormBuilder relationsFacet(RelationFacetList facets) {
        facets().setRelations(facets);
        return this;
    }

    public MediaFormBuilder sortOrder(MediaSortOrder... orders) {
        for (MediaSortOrder order : orders) {
            form.addSortField(order);
        }
        return this;
    }

    public MediaFormBuilder asc(MediaSortField... orders) {
        for (MediaSortField order : orders) {
            form.addSortField(MediaSortOrder.asc(order));
        }
        return this;
    }

    public MediaFormBuilder ageRatingFacet() {
        return ageRatingFacet((Integer) null);
    }


    public MediaFormBuilder ageRatingFacet(Integer threshold) {
        MediaFacet facet = new MediaFacet();
        facet.setThreshold(threshold);
        return ageRatingFacet(facet);
    }

    public MediaFormBuilder ageRatingFacet(MediaFacet facet) {
        facets().setAgeRatings(facet);
        return this;
    }


    public MediaFormBuilder contentRatingsFacet() {
        facets().setContentRatings(new MediaFacet());
        return this;
    }

    /**
     * @since 5.11
     */
    public MediaFormBuilder geoLocationFacet() {
         facets().setGeoLocations(new MediaSearchableTermFacet());
         return this;
    }

    public MediaFormBuilder facetFilter(MediaSearch search) {
        facets().setFilter(search);
        return this;
    }

    public MediaFormBuilder withAllSearches() {

        return  fuzzyText("text")
            .mediaIds(Match.SHOULD, "MID_123", "MID_234")
            .sortDate(
                LocalDateTime.of(2018, 1, 1, 10, 0).atZone(Schedule.ZONE_ID).toInstant(),
                LocalDateTime.of(2018, 3, 1, 17, 0).atZone(Schedule.ZONE_ID).toInstant()
            )
            .publishDate(
                LocalDateTime.of(2018, 1, 1, 10, 0).atZone(Schedule.ZONE_ID).toInstant(),
                LocalDateTime.of(2018, 3, 1, 17, 0).atZone(Schedule.ZONE_ID).toInstant(),
                true
            )
            .broadcasters(
                Match.MUST,
                TextMatcher.must("VPR*", StandardMatchType.WILDCARD),
                TextMatcher.must("EO", StandardMatchType.TEXT)
            )
            .locations(
                "https://download.omroep.nl/test.mp4",
                "mp3"
            )
            .tags(false, "foo", "bar")
            .genres(Match.MUST,
                TextMatcher.must("3.1.6.1.*", StandardMatchType.WILDCARD)
            )
            .types(Match.NOT, MediaType.VISUALRADIOSEGMENT)
            .avTypes(AVType.VIDEO)
            .duration(Duration.ofMinutes(1), Duration.ofMinutes(15))
            .episodeOfs(Match.SHOULD, "MID_456")
            .descendantOfs(Match.SHOULD, "MID_789")
            .memberOfs(Match.SHOULD, "MID_01230")
            .ageRating(AgeRating.ALL)
            .contentRatings(Match.NOT, ContentRating.SEKS)
            .scheduleEvents(
                ScheduleEventSearch.builder()
                    .channel(Channel.NED1)
                    .rerun(false)
                    .end(
                        LocalDateTime.of(2018, 4, 1, 10, 0).atZone(Schedule.ZONE_ID).toInstant()
                    )
                    .build()
            )
            .titles(
                TitleSearch.builder()
                    .type(TextualType.MAIN)
                    .value("Tegenlicht*")
                    .matchType(StandardMatchType.WILDCARD)
                    .build()
            )
            .relation(
                RelationDefinition.of("ALBUM", "VPRO"),
                "FOO",
                null
            )
            .highlight(true);
    }

    public MediaFormBuilder withAllFacets() {
        return broadcasterFacet()
            .genreFacet()
            .tagFacet(false)
            .typeFacet()
            .avTypeFacet(MediaFacet.builder()
                .filter(
                    MediaSearch
                        .builder()
                        .contentRatings(
                            TextMatcherList.must(TextMatcher.must(ContentRating.ANGST.name())))
                        .build()
                )
                .build())
            .sortDateFacet(DateRangePreset.LAST_WEEK, DateRangePreset.THIS_WEEK)
            .durationFacet(
                new DurationRangeInterval("2 minutes"),
                DurationRangeFacetItem
                    .builder()
                    .begin(Duration.ofMinutes(5))
                    .end(Duration.ofMinutes(10))
                    .build())
            .episodeOfFacet()
            .memberOfFacet()
            .descendantOfFacet(MemberRefFacet
                .refBuilder()
                .subSearch(
                    MemberRefSearch.builder()
                        .types(TextMatcherList.must(TextMatcher.not("SERIES")))
                        .build()
                )
                .threshold(10)
                .build()
            )
            .relationsFacet("test")
            .ageRatingFacet()
            .contentRatingsFacet()
            .facetFilter(
                MediaSearch.builder()
                    .creationDates(
                        DateRangeMatcherList.builder()
                            .value(
                                DateRangeMatcher.builder()
                                    .begin(LocalDateTime.of(2010, 1, 1, 12, 0)
                                        .atZone(Schedule.ZONE_ID).toInstant()
                                    )
                                    .build()
                            ).build()
                    )
                    .build()
            )
            ;
    }
    public MediaFormBuilder withEverything() {
        return
            withAllSearches()
            .withAllFacets()
            .sortOrder(
                MediaSortOrder.asc(MediaSortField.creationDate),
                MediaSortOrder.desc(MediaSortField.title),
                TitleSortOrder.builder()
                    .ownerType(OwnerType.NPO)
                    .textualType(TextualType.MAIN)
                    .build()
            )
            ;

    }


    private MediaSearch search() {
        if(form.getSearches() == null) {
            form.setSearches(new MediaSearch());
        }
        return form.getSearches();
    }

    private MediaFacets facets() {
        if(form.getFacets() == null) {
            form.setFacets(new MediaFacets());
        }
        return form.getFacets();
    }


}
