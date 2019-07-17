/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.meeuw.xml.bind.annotation.XmlDocumentation;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.*;
import nl.vpro.domain.api.jackson.media.ScheduleEventSearchListJson;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.AuthorizedDuration;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.Title;
import nl.vpro.domain.user.Broadcaster;

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
                titles
            );
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        return input != null &&
            applyText(input) &&
            applyMediaIds(input) &&
            applyAvTypes(input) &&
            applyTypes(input) &&
            applySortDates(input) &&
            applyLastModifiedDates(input) &&
            applyCreationDates(input) &&
            applyPublishDates(input) &&
            applyBroadcasters(input) &&
            applyLocations(input) &&
            applyTags(input) &&
            applyDurations(input) &&
            applyDescendantOf(input) &&
            applyEpisodeOf(input) &&
            applyMemberOf(input) &&
            applyRelations(input) &&
            applySchedule(input) &&
            applyTitles(input);
    }

    protected boolean applyAvTypes(MediaObject input) {
        AVType avType = input.getAVType();
        if (avType == null) {
            return avTypes == null;
        }
        return Matchers.listPredicate(avTypes).test(input.getAVType().name());
    }


    protected boolean applyTypes(MediaObject input) {
        MediaType mediaType = MediaType.getMediaType(input);
        if (mediaType == null) {
            return types == null;
        }
        return Matchers.listPredicate(types).test(mediaType.name());
    }

    protected boolean applyText(MediaObject input) {
        if (text == null) {
            return true;
        }
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

    }

    protected boolean applyMediaIds(MediaObject input) {
        return Matchers.listPredicate(mediaIds).test(input.getMid());
    }


    protected boolean applySortDates(MediaObject input) {
        return applyDateRange(input, sortDates, MediaObject::getSortInstant);
    }

    protected boolean applyLastModifiedDates(MediaObject input) {
        return applyDateRange(input, lastModifiedDates, MediaObject::getLastModifiedInstant);
    }

    protected boolean applyCreationDates(MediaObject input) {
        return applyDateRange(input, creationDates, MediaObject::getCreationInstant);
    }

    protected boolean applyPublishDates(MediaObject input) {
        return applyDateRange(input, publishDates, MediaObject::getLastPublishedInstant);
    }

    protected boolean applyDateRange(MediaObject input, DateRangeMatcherList range, Function<MediaObject, Instant> inputDateGetter) {
        if (range == null) {
            return true;
        }
        Instant inputDate = inputDateGetter.apply(input);
        return inputDate != null && range.test(inputDate);
    }

    protected boolean applyBroadcasters(MediaObject input) {
        return Matchers.toPredicate(broadcasters, Broadcaster::getId).test(input.getBroadcasters());
    }

    protected boolean applyLocations(MediaObject input) {
        return Matchers.toPredicate(locations, Location::getProgramUrl).test(input.getLocations());
    }

    protected boolean applyTags(MediaObject input) {
        return Matchers.toPredicate(tags, Tag::getText).test(input.getTags());
    }

    protected boolean applyDurations(MediaObject input) {
        return durations == null || durations.test(AuthorizedDuration.get(input.getDuration()));
    }

    protected boolean applyDescendantOf(MediaObject input) {
        return Matchers.toPredicate(descendantOf, DescendantRef::getMidRef).test(input.getDescendantOf());
    }

    protected boolean applyEpisodeOf(MediaObject input) {
        if (!(input instanceof Program)) {
            return false;
        }
        Program program = (Program) input;
        return Matchers.toPredicate(episodeOf, MemberRef::getMidRef).test(program.getEpisodeOf());
    }

    protected boolean applyMemberOf(MediaObject input) {
        return Matchers.toPredicate(memberOf, MemberRef::getMidRef).test(input.getMemberOf());
    }

    protected boolean applyRelations(MediaObject input) {
        if (relations == null) {
            return true;
        }

        for (Relation relation : input.getRelations()) {
            if (relations.test(relation)) {
                return true;
            }
        }
        return false;
    }

    protected boolean applySchedule(MediaObject input) {
        if (scheduleEvents == null) {
            return true;
        }

        for (ScheduleEvent event : input.getScheduleEvents()) {
            for (ScheduleEventSearch search : scheduleEvents) {
                if (search.test(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean applyTitles(MediaObject input) {
        if (titles == null) {
            return true;
        }

        for (Title title : input.getTitles()) {
            for (TitleSearch search : titles) {
                if (search.test(title)) {
                    return true;
                }
            }
        }
        return false;
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
}
