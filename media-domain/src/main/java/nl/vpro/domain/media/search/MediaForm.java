/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.StreamingStatus;
import nl.vpro.domain.user.Organization;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mediaForm")
@XmlType(name = "mediaFormType", propOrder = {
    "pager",
    "broadcasters",
    "portals",
    "organizations",
    "text",
    "titles",
    "types",
    "releaseYear",
    "relations",
    "noBroadcast",
    "scheduleEventsCount",
    "hasLocations",
    "locationsCount",
    "noPlaylist",
    "memberOfCount",
    "sortRange",
    "eventRange",
    "scheduleEventRange",
    "channels",
    "createdBy",
    "creationRange",
    "lastModifiedBy",
    "lastModifiedRange",
    "tags",
    "avType",
    "notAnEpisode",
    "episodeOfCount",
    "noMembers",
    "noCredits",
    "imagesWithoutCreditsCount",
    "imagesCount",
    "findDeleted",
    "excludedMids",
    "descendantOf",
    "streamingPlatformStatuses"

})
public class MediaForm {

    @XmlElement(required = true)
    @Valid
    private MediaPager pager;

    @XmlElement(name = "broadcaster")
    @JsonProperty("broadcasters")
    private Collection<String> broadcasters;

    @XmlElement(name = "portal")
    @JsonProperty("portals")
    private Collection<String> portals;

    /**
     * To search on any of the organizations (so both broadcasters and portals)
     * This is needed because if you use 'broadcasters' and 'portals' seperately, that will result in an AND.
     */
    @XmlElement(name = "organization")
    @JsonProperty("organizations")
    private Collection<Organization> organizations;

    /*
    @XmlElement(name = "writable", namespace = Xmlns.SEARCH_NAMESPACE)
    private boolean writable = false;
    */

    @XmlElement
    private String text;

    @XmlElement(name = "title")
    @JsonProperty("titles")
    private Collection<TitleForm> titles;

    @XmlElement(name = "type")
    @JsonProperty("types")
    private Collection<MediaType> types;

    @XmlElement
    protected Short releaseYear;

    @XmlElement(name = "relation")
    @JsonProperty("relations")
    private Collection<RelationForm> relations;

    @XmlElement
    private Boolean noBroadcast;

    @XmlElement
    @Getter
    @Setter
    private IntegerRange scheduleEventsCount;

    @XmlElement
    private Boolean hasLocations;

    @XmlElement
    @Getter
    @Setter
    private IntegerRange locationsCount;

    @XmlElement
    private Boolean noPlaylist;

    @XmlElement
    private DateRange sortRange;

    @XmlElement
    private DateRange eventRange;

    @XmlElement(name = "channel")
    @JsonProperty("channels")
    private Collection<String> channels;

    @XmlElement
    private String createdBy;

    @XmlElement
    private DateRange creationRange;

    @XmlElement
    private String lastModifiedBy;

    @XmlElement
    private DateRange lastModifiedRange;

    @XmlElement
    private DateRange scheduleEventRange;

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    private Collection<String> tags;

    @XmlElement
    private AVType avType;

    @XmlElement
    private Boolean notAnEpisode;

    @XmlElement
    @Getter
    @Setter
    private IntegerRange memberOfCount;

    @XmlElement
    @Getter
    @Setter
    private IntegerRange episodeOfCount;

    @XmlElement
    private Boolean noMembers;

    @XmlElement
    @Deprecated
    private Boolean noCredits;

    @XmlElement
    @Getter
    @Setter
    private IntegerRange imagesWithoutCreditsCount;

    @XmlElement
    @Getter
    @Setter
    private IntegerRange imagesCount;

    @XmlElement
    private Boolean findDeleted;

    @XmlElement(name = "excludedMid")
    @JsonProperty("excludedMids")
    private Collection<String> excludedMids;

    @XmlAttribute
    @Getter
    @Setter
    private OwnerType forOwner;

    @XmlElement(name = "descendantOf")
    @JsonProperty("descendantOf")
    @Getter
    @Setter
    private Collection<String> descendantOf;

    @XmlElement(name = "streamingPlatformStatus")
    @JsonProperty("streamingPlatformStatus")
    @Getter
    @Setter
    private Collection<StreamingStatus> streamingPlatformStatuses;


    public MediaForm() {
        this(new MediaPager());
    }

    public MediaForm(MediaPager pager) {
        this(pager, (String) null);
    }

    public MediaForm(MediaPager pager, String text) {
        this(pager, null, null, text, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public MediaForm(MediaPager pager, Collection<MediaType> types) {
        this(pager, null, null, null, types, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @lombok.Builder(builderClassName = "Builder")
    MediaForm(
        MediaPager pager,
        Collection<String> broadcasters,
        Collection<String> portals,
        String text,
        Collection<MediaType> types,
        Boolean noBroadcast,
        Boolean hasLocations,
        Boolean noPlaylist,
        DateRange eventRange,
        String createdBy,
        DateRange creationRange,
        String lastModifiedBy,
        DateRange lastModifiedRange,
        IntegerRange locationsCount,
        Boolean notAnEpisode,
        Boolean noMembers,
        Boolean noCredits,
        OwnerType forOwner
        ) {

        if(pager == null) {
            pager = MediaPager.builder().build();
        }

        this.pager = pager;
        this.broadcasters = broadcasters;
        this.portals = portals;
        this.text = text;
        this.types = types;
        this.noPlaylist = noPlaylist;
        this.eventRange = eventRange;
        this.noBroadcast = noBroadcast;
        this.hasLocations = hasLocations;
        this.createdBy = createdBy;
        this.creationRange = creationRange;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedRange = lastModifiedRange;
        this.locationsCount = locationsCount;
        this.notAnEpisode = notAnEpisode;
        this.findDeleted = null; // backwards compatiblity
        this.noMembers = noMembers;
        this.noCredits = noCredits;
        this.forOwner = forOwner;

    }

    public static class Builder {
        Collection<String> broadcasters = new ArrayList<>();
        public Builder broadcasters(Collection<String> b) {
            if (b != null) {
                this.broadcasters.addAll(b);
            }
            return this;

        }
        public Builder broadcaster(String broadcaster) {
            this.broadcasters.add(broadcaster);
            return this;
        }

        public Builder quotedText(String text) {
            return text('"' + text + '"');
        }

        public Builder sortOrder(MediaSortField field) {
            MediaPager p = new MediaPager();
            p.setSort(field);
            return pager(p);
        }
    }

    public MediaPager getPager() {
        return pager;
    }

    public Collection<String> getBroadcasters() {
        return broadcasters;
    }


    public void setBroadcasters(Collection<String> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public boolean hasBroadcasters() {
        return !isEmpty(broadcasters);
    }

    public Collection<String> getPortals() {
        return portals;
    }

    public void setPortals(Collection<String> portals) {
        this.portals = portals;
    }

    public boolean hasPortals() {
        return !isEmpty(portals);
    }

    public String getText() {
        return text;
    }

    public MediaForm setText(String text) {
        this.text = text;
        return this;
    }

    public boolean hasText() {
        return text != null && text.length() > 0;
    }

    public Collection<TitleForm> getTitles() {
        return collection(titles);
    }

    public MediaForm addTitle(TitleForm title) {
        if(titles == null) {
            titles = new ArrayList<>();
        }
        titles.add(title);
        return this;
    }

    public boolean hasTitles() {
        return !isEmpty(titles);
    }

    public Collection<MediaType> getTypes() {
        return collection(types);
    }

    public void setTypes(Collection<MediaType> types) {
        this.types = types;
    }

    public MediaForm addType(MediaType type) {
        if(types == null) {
            types = new ArrayList<>();
        }
        types.add(type);
        return this;
    }

    public Collection<String> getTypesAsStrings() {
        List<String> list = new ArrayList<>();
        for(MediaType type : getTypes()) {
            list.add(type.name());
        }
        return list;
    }

    public MediaForm setReleaseYear(Short s) {
        releaseYear = s;
        return this;
    }

    public Short getReleaseYear() {
        return releaseYear;
    }

    public boolean hasReleaseDate() {
        return releaseYear != null;
    }


    public boolean includeSegments() {
        return getTypes().contains(MediaType.SEGMENT);
    }

    public boolean hasTypes() {
        return types != null && types.size() > 0 && !types.contains(MediaType.MEDIA);
    }

    public Collection<RelationForm> getRelations() {
        return collection(relations);
    }

    public MediaForm addRelation(RelationForm relation) {
        if(relations == null) {
            relations = new ArrayList<>();
        }
        relations.add(relation);
        return this;
    }

    public boolean hasRelations() {
        return !isEmpty(relations);
    }

    @Deprecated
    public boolean hasNoBroadcast() {
        return noBroadcast == null ? false : noBroadcast;
    }

    @Deprecated
    public void setNoBroadcast(boolean noBroadcast) {
        this.noBroadcast = noBroadcast;
    }

    @Deprecated
    public boolean hasLocations() {
        return hasLocations == null ? false : hasLocations;
    }

    @Deprecated
    public void setHasLocations(boolean hasLocations) {
        this.hasLocations = hasLocations;
    }

    @Deprecated
    public boolean hasNoPlaylist() {
        return noPlaylist == null ? false : noPlaylist;
    }

    @Deprecated
    public void setNoPlaylist(boolean noPlaylist) {
        this.noPlaylist = noPlaylist;
    }

    public Boolean hasNoMembers() {
        return noMembers == null ? false : noMembers;
    }

    public void setNoMembers(Boolean noMembers) {
        this.noMembers = noMembers;
    }

    @Deprecated
    public boolean hasNoCredits() {
        return noCredits == null ? false : noCredits;
    }

    @Deprecated
    public void setNoCredits(Boolean noCredits) {
        this.noCredits = noCredits;
    }

    /**
     * @since 3.4
     */
    public DateRange getSortRange() {
        return sortRange;
    }

    public boolean hasSortRange() {
        return sortRange != null && sortRange.hasValues();
    }

    public void setSortRange(DateRange sortRange) {
        this.sortRange = sortRange;
    }


    /**
     * Searches only in 'first showing' event
     */
    public DateRange getEventRange() {
        return eventRange;
    }

    public boolean hasEventRange() {
        return eventRange != null && eventRange.hasValues();
    }

    public Collection<String> getChannels() {
        return channels;
    }

    public void setChannels(Collection<String> channels) {
        this.channels = channels;
    }

    public boolean hasChannels() {
        return !isEmpty(channels);
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public boolean hasCreatedBy() {
        return createdBy != null && createdBy.length() > 0;
    }

    public DateRange getCreationRange() {
        return creationRange;
    }

    public boolean hasCreationRange() {
        return creationRange != null && creationRange.hasValues();
    }

    public void setCreationRange(DateRange creationRange) {
        this.creationRange = creationRange;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public boolean hasLastModifiedBy() {
        return lastModifiedBy != null && lastModifiedBy.length() > 0;
    }

    public DateRange getLastModifiedRange() {
        return lastModifiedRange;
    }

    public void setLastModifiedRange(DateRange lastModifiedRange) {
        this.lastModifiedRange = lastModifiedRange;
    }

    public boolean hasModifiedRange() {
        return lastModifiedRange != null && lastModifiedRange.hasValues();
    }

    /**
     * Searches in all available events.
     */
    public DateRange getScheduleEventRange() {
        return scheduleEventRange;
    }

    public void setScheduleEventRange(DateRange scheduleEventRange) {
        this.scheduleEventRange = scheduleEventRange;
    }

    public boolean hasScheduleEventRange() {
        return scheduleEventRange != null && scheduleEventRange.hasValues();
    }

    public Collection<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Collection<Organization> organizations) {
        this.organizations = organizations;
    }

    public boolean hasOrganizations() {
        return organizations != null;
    }

    public Collection<String> getTags() {
        return tags;
    }

    public boolean hasTags() {
        return tags != null && tags.size() > 0;
    }

    public boolean hasStreamingPlatformStatuses() {
        return streamingPlatformStatuses != null && streamingPlatformStatuses.size() > 0;
    }


    public void setTags(Collection<String> tags) {
        this.tags = tags;
    }

    public AVType getAvType() {
        return avType;
    }

    public void setAvType(AVType avType) {
        this.avType = avType;
    }

    @Deprecated
    public boolean isNotAnEpisode() {
        return notAnEpisode == null ? false : notAnEpisode;
    }

    @Deprecated
    public void setNotAnEpisode(boolean notAnEpisode) {
        this.notAnEpisode = notAnEpisode;
    }

    public boolean isFindDeleted() {
        return findDeleted == null ? true : findDeleted;
    }

    public void setFindDeleted(Boolean findDeleted) {
        this.findDeleted = findDeleted;
    }

    public Collection<String> getExcludedMids() {
        return excludedMids;
    }

    public void setExcludedMids(Collection<String> excludedMids) {
        this.excludedMids = excludedMids;
    }

    public boolean hasExcludedMids() {
        return ! isEmpty(excludedMids);
    }

    private static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private <T> Collection<T> collection(Collection<T> col) {
        if(col == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(col);
        }
    }

    void beforeUnmarshal(Unmarshaller u, Object parent) {
        if (! isNotAnEpisode()) {
            this.notAnEpisode = null;
        }
        if (!hasNoCredits()) {
            this.noCredits = null;
        }
        if (!hasNoMembers()) {
            this.noMembers = null;
        }
        if (!hasNoPlaylist()) {
            this.noPlaylist = null;
        }
        if (!hasNoBroadcast()) {
            this.noBroadcast = null;
        }
        if (!hasLocations()) {
            this.hasLocations = null;
        }

    }

}
