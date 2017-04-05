/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonSetter;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.*;
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
public class MediaSearch extends AbstractTextSearch implements Predicate<MediaObject> {


    @Valid
    private TextMatcherList mediaIds;

    @Valid
    private TextMatcherList types;

    @Valid
    private TextMatcherList avTypes;

    @Valid
    private DateRangeMatcherList sortDates;

    @Valid
    private DateRangeMatcherList publishDates;

    @Valid
    private TextMatcherList broadcasters;

    @Valid
    private TextMatcherList locations;

    @Valid
    private ExtendedTextMatcherList tags;

    @Valid
    private TextMatcherList genres;

    @Valid
    private DateRangeMatcherList durations;

    @Valid
    private TextMatcherList descendantOf;

    @Valid
    private TextMatcherList episodeOf;

    @Valid
    private TextMatcherList memberOf;

    @Valid
    private RelationSearchList relations;

    @Valid
    private ScheduleEventSearch scheduleEvents;

    @Valid
    private TextMatcherList ageRatings;

    @Valid
    private TextMatcherList contentRatings;


    public TextMatcherList getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(TextMatcherList mediaIds) {
        this.mediaIds = mediaIds;
    }

    public TextMatcherList getTypes() {
        return types;
    }

    public void setTypes(TextMatcherList types) {
        this.types = types;
    }


    public TextMatcherList getAvTypes() {
        return avTypes;
    }

    public void setAvTypes(TextMatcherList avTypes) {
        this.avTypes = avTypes;
    }


    public DateRangeMatcherList getSortDates() {
        return sortDates;
    }

    public void setSortDates(DateRangeMatcherList sortDate) {
        this.sortDates = sortDate;
    }

    /**
     * @deprecated For json backwards compatibility
     */
    @JsonSetter
    public void setSortDate(DateRangeMatcherList sortDate) {
        this.sortDates = sortDate;
    }

    public DateRangeMatcherList getPublishDates() {
        return publishDates;
    }

    public void setPublishDates(DateRangeMatcherList publishDates) {
        this.publishDates = publishDates;
    }

    public TextMatcherList getBroadcasters() {
        return broadcasters;
    }

    public void setBroadcasters(TextMatcherList broadcasters) {
        this.broadcasters = broadcasters;
    }

    public TextMatcherList getLocations() {
        return locations;
    }

    public void setLocations(TextMatcherList locations) {
        this.locations = locations;
    }

    public ExtendedTextMatcherList getTags() {
        return tags;
    }

    public void setTags(ExtendedTextMatcherList tags) {
        this.tags = tags;
    }

    public TextMatcherList getGenres() {
        return genres;
    }

    public void setGenres(TextMatcherList genres) {
        this.genres = genres;
    }

    public TextMatcherList getDescendantOf() {
        return descendantOf;
    }

    public DateRangeMatcherList getDurations() {
        return durations;
    }

    public void setDurations(DateRangeMatcherList duration) {
        this.durations = duration;
    }

    public void setDescendantOf(TextMatcherList descendantOf) {
        this.descendantOf = descendantOf;
    }

    public TextMatcherList getEpisodeOf() {
        return episodeOf;
    }

    public void setEpisodeOf(TextMatcherList episodeOf) {
        this.episodeOf = episodeOf;
    }

    public TextMatcherList getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(TextMatcherList memberOf) {
        this.memberOf = memberOf;
    }

    public RelationSearchList getRelations() {
        return relations;
    }

    public void setRelations(RelationSearchList relations) {
        this.relations = relations;
    }

    public ScheduleEventSearch getScheduleEvents() {
        return scheduleEvents;
    }

    public void setScheduleEvents(ScheduleEventSearch scheduleEvents) {
        this.scheduleEvents = scheduleEvents;
    }

    public TextMatcherList getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(TextMatcherList ageRating) {
        this.ageRatings = ageRating;
    }

    public TextMatcherList getContentRatings() {
        return contentRatings;
    }

    public void setContentRatings(TextMatcherList contentRatings) {
        this.contentRatings = contentRatings;
    }

    @Override
    public boolean hasSearches() {
        return mediaIds != null && !mediaIds.isEmpty()
            || sortDates != null && !sortDates.isEmpty()
            || types != null && !types.isEmpty()
            || avTypes != null && !avTypes.isEmpty()
            || broadcasters != null && !broadcasters.isEmpty()
            || locations != null && !locations.isEmpty()
            || tags != null && !tags.isEmpty()
            || durations != null && !durations.isEmpty()
            || descendantOf != null && !descendantOf.isEmpty()
            || episodeOf != null && !episodeOf.isEmpty()
            || memberOf != null && !memberOf.isEmpty()
            || relations != null && !relations.hasSearches()
            || scheduleEvents != null && scheduleEvents.hasSearches()
            || ageRatings != null && !ageRatings.isEmpty()
            || contentRatings != null && !contentRatings.isEmpty();
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        return input != null && (
            applyText(input) &&
            applyMediaIds(input) &&
            applyAvTypes(input)) &&
            applyTypes(input) &&
            applySortDates(input) &&
            applyBroadcasters(input) &&
            applyLocations(input) &&
            applyTags(input) &&
            applyDurations(input) &&
            applyDescendantOf(input) &&
            applyEpisodeOf(input) &&
            applyMemberOf(input) &&
            applyRelations(input)&&
            applySchedule(input);
    }

    protected boolean applyAvTypes(MediaObject input) {
        AVType avType = input.getAVType();
        if(avType == null) {
            return avTypes == null;
        }
        return Matchers.listPredicate(avTypes).test(input.getAVType().name());
    }


    protected boolean applyTypes(MediaObject input) {
        MediaType mediaType = MediaType.getMediaType(input);
        if(mediaType == null) {
            return types == null;
        }
        return Matchers.listPredicate(types).test(mediaType.name());
    }

    protected boolean applyText(MediaObject input) {
        if(text == null) {
            return true;
        }
        for(Title title : input.getTitles()) {
            if(Matchers.tokenizedPredicate(text).test(title.getTitle())) {
                return true;
            }
        }
        for(Description description : input.getDescriptions()) {
            if(Matchers.tokenizedPredicate(text).test(description.getDescription())) {
                return true;
            }
        }
        return false;

    }

    protected boolean applyMediaIds(MediaObject input) {
        return Matchers.listPredicate(mediaIds).test(input.getMid());
    }

    protected boolean applySortDates(MediaObject input) {
        return sortDates == null || sortDates.test(input.getSortDate());
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
        return durations == null || durations.test(input.getDurationAsDate());
    }

    protected boolean applyDescendantOf(MediaObject input) {
        return Matchers.toPredicate(descendantOf, DescendantRef::getMidRef).test(input.getDescendantOf());
    }

    protected boolean applyEpisodeOf(MediaObject input) {
        if(!(input instanceof Program)) {
            return false;
        }
        Program program = (Program)input;
        return Matchers.toPredicate(episodeOf, MemberRef::getMidRef).test(program.getEpisodeOf());
    }

    protected boolean applyMemberOf(MediaObject input) {
        return Matchers.toPredicate(memberOf, MemberRef::getMidRef).test(input.getMemberOf());
    }

    protected boolean applyRelations(MediaObject input) {
        if(relations == null) {
            return true;
        }

        for(Relation relation : input.getRelations()) {
            if(relations.test(relation)) {
                return true;
            }
        }
        return false;
    }

    protected boolean applySchedule(MediaObject input) {
        if(scheduleEvents == null) {
            return true;
        }

        for(ScheduleEvent event : input.getScheduleEvents()) {
            if(scheduleEvents.test(event)) {
                return true;
            }
        }
        return false;
    }
}
