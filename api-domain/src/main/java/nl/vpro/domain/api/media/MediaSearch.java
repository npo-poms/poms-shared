/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.*;

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

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.jackson.media.ScheduleEventSearchListJson;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;

;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaSearchType")
@EqualsAndHashCode(callSuper = true)
@XmlDocumentation("Limits the search result to media with certain properties")
@lombok.AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
public class MediaSearch extends AbstractTextSearch implements Predicate<MediaObject> {


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
        return getTestResult(input).test();

    }

    public TestResult getTestResult(MediaObject input) {
       return  new TestResultCombiner(
           applyText(input),
           applyText(input),
           applyMediaIds(input),
           applyAvTypes(input),
           applyTypes(input),
           applySortDates(input),
           applyLastModifiedDates(input),
           applyCreationDates(input),
           applyPublishDates(input),
           applyBroadcasters(input),
           applyLocations(input),
           applyTags(input),
           applyDurations(input),
           applyDescendantOf(input),
           applyEpisodeOf(input),
           applyMemberOf(input),
           applyRelations(input),
           applySchedule(input),
           applyTitles(input),
           applyGeoLocations(input)
       );
    }


    protected TestResult applyAvTypes(MediaObject input) {
        return TestResult.of("avtypes", avTypes, () -> name(input.getAVType()));
    }


    protected static String name(Enum<?> eValue) {
        return eValue == null ? null : eValue.name();
    }

    protected TestResult applyTypes(MediaObject input) {
        return TestResult.of("types", types, () -> name(MediaType.getMediaType(input)));
    }

    protected TestResult applyText(MediaObject input) {
        if (text == null || StringUtils.isBlank(text.getValue())) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("text", text.getMatch(), () -> {
            for (Title title : input.getTitles()) {
                if (Matchers.tokenizedPredicate(text).test(title.get())) {
                    return true;
                }
            }
            for (Description description : input.getDescriptions()) {
                if (Matchers.tokenizedPredicate(text).test(description.get())) {
                    return true;
                }
            }
            return false;
        });
    }

    protected TestResult applyMediaIds(MediaObject input) {
        return TestResult.of("ids", mediaIds, input::getMid);
    }


    protected TestResult  applySortDates(MediaObject input) {
        if (sortDates == null) {
            return TestResultIgnore.INSTANCE;
        }
        return new TestResultImpl("sortdates",
            sortDates.getMatch(), applyDateRange(input, sortDates, MediaObject::getSortInstant));
    }

    protected TestResult applyLastModifiedDates(MediaObject input) {
        return TestResultIgnore.INSTANCE;
        // TODO
        //return TestResult.of(lastModifiedDates, applyDateRange(input, lastModifiedDates, MediaObject::getLastModifiedInstant));
    }

    protected TestResult applyCreationDates(MediaObject input) {
        return TestResultIgnore.INSTANCE;
        // TODO
        //return applyDateRange(input, creationDates, MediaObject::getCreationInstant);
    }

    protected TestResult applyPublishDates(MediaObject input) {
        return TestResultIgnore.INSTANCE;
        // TODO
        //return applyDateRange(input, publishDates, MediaObject::getLastPublishedInstant);
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
        return  TestResult.of("broadcasters", broadcasters, Broadcaster::getId, input::getBroadcasters);
    }

    protected TestResult applyLocations(MediaObject input) {
        return TestResult.of("locations", locations, Location::getProgramUrl, input::getLocations);
    }

    protected TestResult applyTags(MediaObject input) {
        return TestResult.of("tags", tags, Tag::getText, input::getTags);
    }

    protected TestResult applyDurations(MediaObject input) {
        return TestResultIgnore.INSTANCE;
        // TODO
        //return TestResult.of(durations, () -> AuthorizedDuration.get(input.getDuration()));
    }

    protected TestResult applyDescendantOf(MediaObject input) {
        return TestResult.of("descendantof",
            descendantOf,
            DescendantRef::getMidRef,
            input::getDescendantOf);
    }

    protected TestResult applyEpisodeOf(MediaObject input) {
        if (episodeOf == null) {
            return TestResultIgnore.INSTANCE;
        }
        if (!(input instanceof Program)) {
            return new TestResultImpl("episodeof", episodeOf.getMatch(), () -> false);
        }
        Program program = (Program) input;
        return TestResult.of("episodeof", episodeOf, MemberRef::getMidRef,  program::getEpisodeOf);
    }

    protected TestResult applyMemberOf(MediaObject input) {
        return TestResult.of("memberof", memberOf, MemberRef::getMidRef, input::getMemberOf);
    }

    protected TestResult applyRelations(MediaObject input) {
        if (relations == null) {
            return TestResultIgnore.INSTANCE;
        }
        TestResultCombiner combiner = new TestResultCombiner();
     /*   TODO
       for (Relation relation : input.getRelations()) {
            if (relations.test(relation)) {
                return true;
            }
        }*/

        return TestResultIgnore.INSTANCE;
    }

    protected TestResult applySchedule(MediaObject input) {
        if (scheduleEvents == null) {
            return TestResultIgnore.INSTANCE;
        }

       /* TODO
           if (input instanceof Program) {
            for (ScheduleEvent event : ((Program) input).getScheduleEvents()) {
                for (ScheduleEventSearch search : scheduleEvents) {
                    if (search.test(event)) {
                        return true;
                    }
                }
            }
        } else {

        }*/
        return TestResultIgnore.INSTANCE;
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

        public Builder title(TitleSearch title) {
            if (titles == null) {
                titles = new ArrayList<>();
            }
            titles.add(title);
            return this;
        }

    }


    public  interface TestResult {

        BooleanSupplier getTest();

        default boolean test() {
            return getTest().getAsBoolean();
        }
        String getDescription();

        //TestResult andThen(TestResult test);
        static TestResult of (
            String description,
            final AbstractTextMatcherList<? extends AbstractTextMatcher<?>, ?> list,
            BooleanSupplier supplier) {
            if (list == null) {
                return TestResultIgnore.INSTANCE;
            }
            return new TestResultImpl(description, list.getMatch(), supplier);
        }

         static TestResult of(
             String description,
             final AbstractTextMatcherList<? extends AbstractTextMatcher<?>, ?> list,
             Supplier<String> supplier) {
             if (list == null) {
                 return TestResultIgnore.INSTANCE;
            }
            return new TestResultImpl(description, list.getMatch(), () -> {
                String value = supplier.get();
                return list.test(value);
            }
            );
        }



        static <T, S extends MatchType> TestResult of(
            String description,
            @Nullable final AbstractTextMatcherList<? extends AbstractTextMatcher<S>, S> textMatchers,
            @NonNull final Function<T, String> textValueGetter,
            Supplier<Collection<T>> supplier) {
            if (textMatchers == null) {
                return TestResultIgnore.INSTANCE;
            }
            return new TestResultImpl(description,
                textMatchers.getMatch(),
                () ->
                    Matchers.toPredicate(textMatchers, textValueGetter)
                        .test(supplier.get()));
        }

    }
    public static class TestResultImpl implements TestResult {

        @Getter
        private final Match match;

        @Getter
        private final BooleanSupplier test;

        @Getter
        private final String description;

        public TestResultImpl(String description, Match match, BooleanSupplier test) {
            this.match = match;
            this.test = test;
            this.description = description;
        }
        @Override
        public String toString() {
            return match + ":" + description;
        }

    }

    public static class TestResultIgnore implements  TestResult {

        public static final TestResultIgnore INSTANCE = new TestResultIgnore();

        @Override
        public BooleanSupplier getTest() {
            return () -> true;
        }

        @Override
        public String getDescription() {
            return "ignored";

        }
    }

    public static class TestResultCombiner implements TestResult {

        @Getter
        private final List<TestResult> shoulds = new ArrayList<>();
        @Getter
        private final List<TestResult> musts = new ArrayList<>();

        String failure;

        public TestResultCombiner(TestResult... tests) {
            add(tests);
        }

        @Override
        public BooleanSupplier getTest() {
            return () -> {
                for (TestResult m : musts) {
                    if (! m.test()) {
                        failure = m.getDescription();
                        return false;
                    }
                }
                if (shoulds.isEmpty()) {
                    return true;
                }
                StringBuilder failureBuilder = new StringBuilder();
                for (TestResult s : shoulds) {
                    if (s.test()) {
                        return true;
                    } else {
                        failureBuilder.append(s.getDescription());
                    }
                }
                failure = failureBuilder.toString();
                return false;
            };
        }

        @Override
        public String getDescription() {
            return "shoulds:" + shoulds + "musts:" + musts + (failure == null ? "": ":" + failure);

        }


        public void add(TestResult... tests) {
            for (TestResult test : tests) {
                if (test instanceof TestResultImpl) {
                    TestResultImpl impl = (TestResultImpl) test;
                    switch (impl.getMatch()) {
                        case MUST:
                            musts.add(test);
                            break;
                        case SHOULD:
                            shoulds.add(test);
                            break;
                        case NOT:
                            musts.add(new TestResultImpl("!" + test.getDescription(), Match.MUST, () -> !test.test()));

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
}
