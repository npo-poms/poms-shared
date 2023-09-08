/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.function.*;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.xml.bind.annotation.XmlDocumentation;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.jackson.media.ScheduleEventSearchListJson;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.util.Truthiness;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaSearchType")
@EqualsAndHashCode(callSuper = true)
@XmlDocumentation("Limits the search result to media with certain properties")
@lombok.AllArgsConstructor
@lombok.Builder(builderClassName = "Builder", buildMethodName = "_build")
@Slf4j
public class MediaSearch extends AbstractTextSearch<MediaObject>  {


    public MediaSearch() {
        super();
    }

    @Valid
    @Getter
    @Setter
    @XmlDocumentation("The MID must match one of the mediaIds")
    private TextMatcherList mediaIds;

    @Valid
    @Getter
    @Setter
    @XmlDocumentation("The media type must match one of these.")
    private TextMatcherList types;

    @Valid
    @Getter
    @Setter
    private TextMatcherList avTypes;

    @Valid
    @Getter
    @Setter
    private DateRangeMatcherList sortDates;

    @Valid
    @Getter
    @Setter
    private DateRangeMatcherList publishDates;

    @Valid
    @Getter
    @Setter
    private DateRangeMatcherList creationDates;

    @Valid
    @Getter
    @Setter
    private DateRangeMatcherList lastModifiedDates;


    @Valid
    @Getter
    @Setter
    private TextMatcherList broadcasters;

    @Valid
    @Getter
    @Setter
    private TextMatcherList locations;

    @Valid
    @Getter
    @Setter
    private ExtendedTextMatcherList tags;

    @Valid
    @Getter
    @Setter
    private TextMatcherList genres;

    @Valid
    @Getter
    @Setter
    private DurationRangeMatcherList durations;

    @Valid
    @Getter
    @Setter
    private TextMatcherList descendantOf;

    @Valid
    @Getter
    @Setter
    private TextMatcherList episodeOf;

    @Valid
    @Getter
    @Setter
    private TextMatcherList memberOf;

    @Valid
    @Getter
    @Setter
    private RelationSearchList relations;

    @Valid
    @Getter
    @Setter
    @JsonSerialize(using = ScheduleEventSearchListJson.Serializer.class)
    @JsonDeserialize(using = ScheduleEventSearchListJson.Deserializer.class)
    private List<ScheduleEventSearch> scheduleEvents;


    @Valid
    @Getter
    @Setter
    private TextMatcherList ageRatings;

    @Valid
    @Getter
    @Setter
    private TextMatcherList contentRatings;

    @Valid
    @Getter
    @Setter
    private List<TitleSearch> titles;

    @Valid
    @Getter
    @Setter
    private List<GeoLocationSearch> geoLocations;

    /**
     * @deprecated For json backwards compatibility
     */
    @JsonSetter
    @Deprecated
    public void setSortDate(DateRangeMatcherList sortDate) {
        this.sortDates = sortDate;
    }


    @Override
    public boolean hasSearches() {
        return text != null ||
            atLeastOneHasSearches(
                mediaIds,
                types,
                avTypes,
                sortDates,
                publishDates,
                creationDates,
                lastModifiedDates,
                broadcasters,
                locations,
                tags,
                genres,
                durations,
                descendantOf,
                episodeOf,
                memberOf,
                relations,
                scheduleEvents,
                ageRatings,
                contentRatings,
                titles,
                geoLocations
            );
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        if (input == null) {
            return false;
        }
        return getTestResult(input).test().getAsBoolean();

    }

    public TestResult getTestResult(MediaObject input) {
       return  new TestResultCombiner(
           applyText(input),
           applyMediaIds(input),
           applyTypes(input),
           applyAvTypes(input),
           applySortDates(input),
           applyPublishDates(input),
           applyCreationDates(input),
           applyLastModifiedDates(input),
           applyBroadcasters(input),
           applyLocations(input),
           applyTags(input),
           applyGenres(input),
           applyDurations(input),
           applyDescendantOf(input),
           applyEpisodeOf(input),
           applyMemberOf(input),
           applyRelations(input),
           applySchedule(input),
           applyAgeRatings(input),
           applyContentRatings(input),
           applyTitles(input),
           applyGeoLocations(input)
       );
    }


    protected TestResult applyAvTypes(MediaObject input) {
        return TestResult.ofSingular("avtypes",
            avTypes,
            MediaSearch::name,
            input::getAVType
        );
    }


    protected static String name(Enum<?> eValue) {
        return eValue == null ? null : eValue.name();
    }

    protected TestResult applyTypes(MediaObject input) {
        return TestResult.ofSingular(
            "types",
            types,
            MediaSearch::name,
            () -> MediaType.getMediaType(input));
    }

    protected TestResult applyText(MediaObject input) {
        if (text == null || StringUtils.isBlank(text.getValue())) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("text", text.getMatch(), () -> {
            for (Title title : input.getTitles()) {
                if (Matchers.tokenizedPredicate(text).test(title.get())) {
                    return Truthiness.TRUE;
                }
            }
            for (Description description : input.getDescriptions()) {
                if (Matchers.tokenizedPredicate(text).test(description.get())) {
                    return Truthiness.TRUE;
                }
            }
            for (Image image : input.getImages()) {
                if (Matchers.tokenizedPredicate(text).test(image.getTitle())) {
                    return Truthiness.TRUE;
                }
            }
            for (Credits credits : input.getCredits()) {
                if (Matchers.tokenizedPredicate(text).test(credits.getName())) {
                    return Truthiness.TRUE;
                }
            }
            // this is not fully implemented. See nl.vpro.domain.api.media.ESMediaQueryBuilder#SEARCH_FIELDS, this would be rather complex
            // especially with respect to stemming this will be near impossible
            return Truthiness.UNKNOWN;
        });
    }

    protected TestResult applyMediaIds(MediaObject input) {
        return TestResult.ofSingular("ids", mediaIds,
            input::getMid);
    }


    protected TestResult applyAgeRatings(MediaObject input) {
        return TestResult.ofSingular("ageratings", ageRatings,
            MediaSearch::name,
            input::getAgeRating
        );
    }

    protected TestResult  applySortDates(MediaObject input) {
        if (sortDates == null) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("sortdates",
            sortDates.getMatch(), applyDateRange(input, sortDates, MediaObject::getSortInstant));
    }

    protected TestResult applyLastModifiedDates(MediaObject input) {
        if (lastModifiedDates == null) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("lastModifiedDates",
            lastModifiedDates.getMatch(), applyDateRange(input, lastModifiedDates, MediaObject::getLastModifiedInstant));
    }

    protected TestResult applyCreationDates(MediaObject input) {

        if (creationDates == null) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("creationDates",
            creationDates.getMatch(), applyDateRange(input, creationDates, MediaObject::getCreationInstant));
    }

    protected TestResult applyPublishDates(MediaObject input) {
        if (publishDates == null) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("publishDates",
            publishDates.getMatch(), applyDateRange(input, publishDates, MediaObject::getLastPublishedInstant));
    }


    protected BooleanSupplier applyDateRange(
        MediaObject input,
        DateRangeMatcherList range,
        Function<MediaObject, Instant> inputDateGetter) {
        return () -> {
            if (range == null) {
                return true;
            }
            Instant inputDate = inputDateGetter.apply(input);
            return inputDate != null && range.test(inputDate);
        };
    }

    protected TestResult applyBroadcasters(MediaObject input) {
        return  TestResult.ofPlural("broadcasters", broadcasters, Broadcaster::getId, input::getBroadcasters);
    }

    protected TestResult applyLocations(MediaObject input) {

        if (locations == null) {
            return TestResultIgnore.INSTANCE;
        }
        TestResultCombiner combiner = new TestResultCombiner();
        for (TextMatcher matcher : locations) {
            combiner.add(new TestResultImpl("locations", matcher.getMatch(), () -> {
                for (Location l : input.getLocations()) {
                    String programUrl = l.getProgramUrl();
                    if (matcher.test(programUrl)) {
                        return true;
                    }
                    int i = programUrl.lastIndexOf('.');
                    if (i >= 0) {
                        String extension = programUrl.substring(i + 1).toLowerCase();
                        if (matcher.getValue().toLowerCase().equals(extension)) {
                            return true;
                        }
                    }
                }
                return false;
            }));
        }
        return combiner;
    }

    protected TestResult applyTags(MediaObject input) {
        return TestResult.ofPlural("tags", tags, Tag::getText, input::getTags);
    }

    protected TestResult applyGenres(MediaObject input) {
        return TestResult.ofPlural("genres", genres, Genre::getTermId, input::getGenres);
    }

    protected TestResult applyContentRatings(MediaObject input) {
        return TestResult.ofPlural("contentRatings", contentRatings, Enum::name, input::getContentRatings);
    }

    protected TestResult applyDurations(MediaObject input) {
        return TestResult.ofSingular("duration",
            durations,
            () -> AuthorizedDuration.get(input.getDuration())
        );
    }

    protected TestResult applyDescendantOf(MediaObject input) {
        return TestResult.ofPlural("descendantof",
            descendantOf,
            DescendantRef::getMidRef,
            input::getDescendantOf);
    }

    protected TestResult applyEpisodeOf(MediaObject input) {
        if (episodeOf == null) {
            return TestResultIgnore.INSTANCE;
        }
        if (!(input instanceof Program program)) {
            return new TestResultImpl("episodeof", episodeOf.getMatch(), () -> false);
        }
        return TestResult.ofPlural("episodeof", episodeOf, MemberRef::getMidRef,  program::getEpisodeOf);
    }

    protected TestResult applyMemberOf(MediaObject input) {
        return TestResult.ofPlural("memberof", memberOf, MemberRef::getMidRef, input::getMemberOf);
    }



    protected TestResult applyRelations(MediaObject input) {
        if (relations == null) {
            return TestResultIgnore.INSTANCE;
        }
        TestResultCombiner combiner = new TestResultCombiner();
        for (RelationSearch rs : relations) {
            combiner.add(new TestResultImpl("relation", rs.getMatch(), () -> {
                for (Relation r : input.getRelations()) {
                    if (rs.test(r)) {
                        return true;
                    }
                }
                return false;
            }));
        }
        return combiner;
    }

    protected TestResult applySchedule(MediaObject input) {
        if (scheduleEvents == null) {
            return TestResultIgnore.INSTANCE;
        }
        TestResultCombiner combiner = new TestResultCombiner();
        if (input instanceof Program) {
            for (ScheduleEventSearch s : scheduleEvents) {
                if (s == null) {
                    log.warn("null in {}", scheduleEvents);
                    continue;
                }
                combiner.add(new TestResultImpl("schedule", s.getMatch(),
                    () -> {
                        for (ScheduleEvent event : ((Program) input).getScheduleEvents()) {
                            if (s.test(event)) {
                                return true;
                            }
                        }
                        return false;
                }));
            }
        } else {
            for (ScheduleEventSearch s : scheduleEvents) {
                combiner.add(new TestResultImpl("schedule", s.getMatch(), () -> false));
            }
        }
        return combiner;

    }

    protected TestResult applyTitles(MediaObject input) {
        if (titles == null) {
            return TestResultIgnore.INSTANCE;
        }
        TestResultCombiner combiner = new TestResultCombiner();
        for (TitleSearch search : titles) {
            combiner.add(new TestResultImpl("titles:" + search.value, search.getMatch(), () -> {
                for (Title title : input.getTitles()) {
                    if (search.test(title)) {
                        return true;
                    }
                }
                return false;
                })
            );
        }
        return TestResultIgnore.INSTANCE;
    }



    protected TestResult applyGeoLocations(MediaObject input) {
        if (geoLocations == null) {
            return TestResultIgnore.INSTANCE;
        }
        // TODO
        for (GeoLocations title : input.getGeoLocations()) {

        }
        return TestResultIgnore.INSTANCE;
    }

    public static class Builder {

        protected SimpleTextMatcher text;

        public Builder text(SimpleTextMatcher text) {
            this.text = text;
            return this;
        }

        public Builder title(TitleSearch title) {
            if (titles == null) {
                titles = new ArrayList<>();
            }
            titles.add(title);
            return this;
        }
        public MediaSearch build() {
            MediaSearch mediaSearch = _build();
            mediaSearch.setText(text);
            return mediaSearch;
        }

    }


    public  interface TestResult {

        Supplier<Truthiness> getTest();

        default Truthiness test() {
            return getTest().get();
        }
        String getDescription();

        //TestResult andThen(TestResult test);
        static <V, T extends Matcher<V>, I> TestResult ofSingular(
            String description,
            final MatcherList<V, T> list,
            @NonNull final Function<I, V> valueGetter,
            Supplier<I> supplier) {
            if (list == null) {
                return TestResultIgnore.INSTANCE;
            }
            return new TestResultImpl(description,
                list.getMatch(),
                () -> {
                    V value = valueGetter.apply(supplier.get());
                    return list.test(value);
                }
            );
        }
        static <V, T extends Matcher<V>> TestResult ofSingular(
            String description,
            final MatcherList<V, T> list,
            Supplier<V> supplier) {
            return ofSingular(description, list, s -> s, supplier);
        }




        static <V, T extends Matcher<V>, I> TestResult ofPlural(
            String description,
            @Nullable final MatcherList<V, T> matchers,
            @NonNull final Function<I, V> valueGetter,
            Supplier<Collection<I>> supplier) {
            if (matchers == null) {
                return TestResultIgnore.INSTANCE;
            }
            return new TestResultImpl(description,
                matchers.getMatch(),
                () ->
                    Matchers.toCollectionPredicate(matchers, valueGetter)
                        .test(supplier.get()));
        }

    }
    public static class TestResultImpl implements TestResult {

        @Getter
        private final Match match;

        @Getter
        private final Supplier<Truthiness> test;

        @Getter
        private final String description;

        public TestResultImpl(String description, Match match, Supplier<Truthiness> test) {
            this.match = match;
            this.test = test;
            this.description = description;
        }
        public TestResultImpl(String description, Match match, BooleanSupplier test) {
            this(description, match, () -> of(test.getAsBoolean(), match));
        }
        @Override
        public String toString() {
            return match + ":" + description;
        }

        static Truthiness of(boolean value, Match match) {
            if (match == Match.NOT) {
                value = ! value;
                match = Match.MUST;
            }
            return switch (match) {
                case MUST -> value ? Truthiness.TRUE : Truthiness.FALSE;
                case SHOULD -> value ? Truthiness.TRUE : Truthiness.MAYBE_NOT;
                default -> throw new IllegalStateException();
            };

        }

    }

    public static class TestResultIgnore implements  TestResult {

        public static final TestResultIgnore INSTANCE = new TestResultIgnore();

        @Override
        public Supplier<Truthiness> getTest() {
            return () -> Truthiness.TRUE;
        }

        @Override
        public String getDescription() {
            return "ignored";
        }
        @Override
        public String toString() {
            return "IGNORE";
        }
    }

    /**
     * This calls tries to perform the quite complicated task to combine 'truthiness' as elasticsearch does.
     * <p>
     * if there are only 'must' clauses, this is quite simple.
     *
     */
    @ToString
    public static class TestResultCombiner implements TestResult {

        @Getter
        private final List<TestResult> shoulds = new ArrayList<>();
        @Getter
        private final List<TestResult> musts = new ArrayList<>();

        String failure;
        Truthiness result = null;

        public TestResultCombiner(TestResult... tests) {
            add(tests);
        }

        @Override
        public Supplier<Truthiness> getTest() {
            return () -> {
                result = Truthiness.TRUE;
                if (! musts.isEmpty()) {
                    for (TestResult m : musts) {
                        Truthiness test = m.test();
                        if (test.ordinal() > result.ordinal()) {
                            result = test;
                        }
                        if (!m.test().getAsBoolean()) {
                            failure = m.getDescription();
                            //return Truthiness.FALSE;
                        }
                    }
                    if (!result.getAsBoolean()) {
                        return result;
                    }
                }
                if (shoulds.isEmpty()) {
                    return result;
                }
                result = Truthiness.FALSE;
                StringBuilder failureBuilder = new StringBuilder();
                for (TestResult s : shoulds) {
                    Truthiness test = s.test();
                    if (test.ordinal() < result.ordinal()) {
                        result = test;
                    }
                    if (test.getAsBoolean()) {
                        if (result == Truthiness.TRUE) {
                            break;
                        }
                    } else {
                        failureBuilder.append(s.getDescription());
                    }
                }
                failure = failureBuilder.toString();
                if (!musts.isEmpty() && result == Truthiness.MAYBE_NOT) {
                    result = Truthiness.PROBABLY;
                }
                return result;
            };
        }

        @Override
        public String getDescription() {
            return "shoulds:" + shoulds + ", musts:" + musts + (result == null ? " (not yet determined) " : " ->" + result) +  (StringUtils.isBlank(failure) ? "": ", fails:" + failure);
        }


        public void add(TestResult... tests) {
            for (TestResult test : tests) {
                if (test instanceof TestResultImpl impl) {
                    switch (impl.getMatch()) {
                        case MUST -> musts.add(test);
                        case SHOULD -> shoulds.add(test);
                        case NOT ->
                            musts.add(new TestResultImpl("!" + test.getDescription(), Match.MUST, () -> !test.test().getAsBoolean()));
                    }
                } else if (test instanceof TestResultCombiner) {
                    musts.addAll(((TestResultCombiner) test).getMusts());
                    shoulds.addAll(((TestResultCombiner) test).getShoulds());
                } else if (test instanceof TestResultIgnore) {
                    //ignore
                } else {
                    throw new IllegalStateException();
                }
            }

        }
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("text", text)
            .add("mediaIds", mediaIds)
            .add("types", types)
            .add("avTypes", avTypes)
            .add("sortDates", sortDates)
            .add("publishDates", publishDates)
            .add("creationDates", creationDates)
            .add("lastModifiedDates", lastModifiedDates)
            .add("broadcasters", broadcasters)
            .add("locations", locations)
            .add("tags", tags)
            .add("genres", genres)
            .add("durations", durations)
            .add("descendantOf", descendantOf)
            .add("episodeOf", episodeOf)
            .add("memberOf", memberOf)
            .add("relations", relations)
            .add("scheduleEvents", scheduleEvents)
            .add("ageRatings", ageRatings)
            .add("contentRatings", contentRatings)
            .add("titles", titles)
            .add("geoLocations", geoLocations)
            .omitNullValues()
            .toString();
    }
}
