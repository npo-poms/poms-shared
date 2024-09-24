/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
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
    "nets",
    "createdBy",
    "creationRange",
    "lastModifiedBy",
    "lastModifiedRange",
    "lastPublishedRange",
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
    "ids",
    "descendantOf",
    "streamingPlatformStatuses"

})
@ToString
public class MediaForm {

    @Getter
    @XmlElement(required = true)
    @Valid
    @NonNull
    private MediaPager pager = new MediaPager();

    @Setter
    @Getter
    @XmlElement(name = "broadcaster")
    @JsonProperty("broadcasters")
    private Collection<String> broadcasters;

    @Getter
    @Setter
    @XmlElement(name = "portal")
    @JsonProperty("portals")
    private Collection<String> portals;

    /**
     * To search on any of the organizations (so both broadcasters and portals)
     * This is needed because if you use 'broadcasters' and 'portals' seperately, that will result in an AND.
     */
    @Getter
    @Setter
    @XmlElement(name = "organization")
    @JsonProperty("organizations")
    private Collection<Organization> organizations;

    /*
    @XmlElement(name = "writable", namespace = Xmlns.SEARCH_NAMESPACE)
    private boolean writable = false;
    */

    @Getter
    @XmlElement
    private String text;

    @XmlElement(name = "title")
    @JsonProperty("titles")
    private Collection<TitleForm> titles;

    @Setter
    @XmlElement(name = "type")
    @JsonProperty("types")
    private Collection<MediaType> types;

    @Getter
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

    /**
     * -- GETTER --
     *
     */
    @Getter
    @Setter
    @XmlElement
    private InstantRange sortRange;

    /**
     * -- GETTER --
     *  Searches only in 'first showing' event
     */
    @Getter
    @XmlElement
    private InstantRange eventRange;

    @XmlElement(name = "channel")
    @JsonProperty("channels")
    @Getter
    @Setter
    private Collection<Channel> channels;

    @XmlElement(name = "net")
    @JsonProperty("nets")
    @Getter
    @Setter
    private Collection<Net> nets;

    @XmlElement
    @Getter
    @Setter
    private EditorSearch createdBy;

    @Getter
    @Setter
    @XmlElement
    private InstantRange creationRange;

    @XmlElement
    @Getter
    @Setter
    private EditorSearch lastModifiedBy;

    @Getter
    @Setter
    @XmlElement
    private InstantRange lastModifiedRange;

    /**
     * -- GETTER --
     *  Searches in all available events.
     */
    @Getter
    @Setter
    @XmlElement
    private InstantRange scheduleEventRange;

    @XmlElement
    @Getter
    @Setter
    private InstantRange lastPublishedRange;

    @Getter
    @Setter
    @XmlElement(name = "tag")
    @JsonProperty("tags")
    private Collection<String> tags;

    @Getter
    @Setter
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

    @Setter
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

    @Setter
    @XmlElement
    @Getter
    private Boolean findDeleted;

    @Getter
    @Setter
    @XmlElement(name = "excludedMid")
    @JsonProperty("excludedMids")
    private Collection<String> excludedMids;

    @XmlElement(name = "ids")
    @JsonProperty("ids")
    @Getter
    @Setter
    private Collection<String> ids;

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
    private Collection<StreamingStatusImpl> streamingPlatformStatuses;

    public MediaForm() {
        // for jaxb
    }

    public MediaForm(MediaPager pager) {
        this.pager = pager;
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
        InstantRange eventRange,
        String createdBy,
        String createdById,
        InstantRange creationRange,
        String lastModifiedBy,
        String lastModifiedById,
        InstantRange lastModifiedRange,
        InstantRange lastPublishedRange,
        IntegerRange locationsCount,
        Boolean notAnEpisode,
        Boolean noMembers,
        Boolean noCredits,
        OwnerType forOwner,
        Collection<String> ids,
        List<Channel> channels
        ) {

        if(pager == null) {
            pager = MediaPager.builder().build();
        }
        if (pager.getMax() == null) {
            pager.setMax(50);
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
        this.createdBy = createdById != null ? EditorSearch.id(createdById) : EditorSearch.name(createdBy);
        this.creationRange = creationRange;
        this.lastModifiedBy = lastModifiedById != null ? EditorSearch.id(lastModifiedById) : EditorSearch.name(lastModifiedBy);
        this.lastModifiedRange = lastModifiedRange;
        this.lastPublishedRange = lastPublishedRange;
        this.locationsCount = locationsCount;
        this.notAnEpisode = notAnEpisode;
        this.findDeleted = null; // backwards compatiblity
        this.noMembers = noMembers;
        this.noCredits = noCredits;
        this.forOwner = forOwner;
        this.ids = ids;

    }

    public static class Builder {
        Collection<String> broadcasters = new ArrayList<>();
        MediaPager pager = new MediaPager();

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

        public Builder asc(MediaSortField field) {
            pager.setSort(field);
            pager.setOrder(Pager.Direction.ASC);
            return this;
        }

        public Builder desc(MediaSortField field) {
            pager.setSort(field);
            pager.setOrder(Pager.Direction.DESC);
            return this;
        }

        public Builder max(Integer max) {
            pager.setMax(max);
            return this;
        }
    }


    public boolean hasBroadcasters() {
        return has(broadcasters);
    }

    public boolean hasPortals() {
        return has(portals);
    }

    public MediaForm setText(String text) {
        this.text = text;
        return this;
    }

    public boolean hasText() {
        return text != null && !text.isEmpty();
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
        return has(titles);
    }

    public Collection<MediaType> getTypes() {
        return collection(types);
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

    public boolean hasReleaseDate() {
        return releaseYear != null;
    }


    public boolean includeSegments() {
        return getTypes().contains(MediaType.SEGMENT);
    }

    public boolean hasTypes() {
        return types != null && !types.isEmpty() && !types.contains(MediaType.MEDIA);
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

    /**
     * @deprecated  {@link #setScheduleEventsCount(IntegerRange)} (IntegerRange)}
     */
    @Deprecated
    public boolean hasNoBroadcast() {
        return noBroadcast != null && noBroadcast;
    }

    @Deprecated
    public void setNoBroadcast(boolean noBroadcast) {
        this.noBroadcast = noBroadcast;
    }

    /**
     * @deprecated  {@link #setLocationsCount(IntegerRange)}
     */

    @Deprecated
    public boolean hasLocations() {
        return hasLocations != null && hasLocations;
    }

    /**
     * @deprecated  {@link #setLocationsCount(IntegerRange)}
      */
    @Deprecated
    public void setHasLocations(boolean hasLocations) {
        this.hasLocations = hasLocations;
    }

    /**
     * @deprecated  {@link #setMemberOfCount(IntegerRange)}
     */
    @Deprecated
    public boolean hasNoPlaylist() {
        return noPlaylist != null && noPlaylist;
    }

    /**
     * @deprecated  {@link #setMemberOfCount(IntegerRange)}
     */
    @Deprecated
    public void setNoPlaylist(boolean noPlaylist) {
        this.noPlaylist = noPlaylist;
    }


    public Boolean hasNoMembers() {
        return noMembers != null && noMembers;
    }

    /**
     * @deprecated  ?
     */
    @Deprecated
    public boolean hasNoCredits() {
        return noCredits != null && noCredits;
    }

    @Deprecated
    public void setNoCredits(Boolean noCredits) {
        this.noCredits = noCredits;
    }

    public boolean hasSortRange() {
        return sortRange != null && sortRange.hasValues();
    }


    public boolean hasEventRange() {
        return eventRange != null && eventRange.hasValues();
    }


    public boolean hasChannels() {
        return has(channels);
    }

    public boolean hasNets() {
        return has(nets);
    }

    public boolean hasCreatedBy() {
        return createdBy != null && StringUtils.isNotBlank(createdBy.getText());
    }

    public boolean hasCreationRange() {
        return creationRange != null && creationRange.hasValues();
    }

    public boolean hasLastModifiedBy() {
        return lastModifiedBy != null && StringUtils.isNotBlank(lastModifiedBy.getText());
    }

    public boolean hasModifiedRange() {
        return lastModifiedRange != null && lastModifiedRange.hasValues();
    }

    public boolean hasScheduleEventRange() {
        return scheduleEventRange != null && scheduleEventRange.hasValues();
    }

    public boolean hasOrganizations() {
        return organizations != null;
    }

    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }

    public boolean hasStreamingPlatformStatuses() {
        return streamingPlatformStatuses != null && !streamingPlatformStatuses.isEmpty();
    }


    @Deprecated
    public boolean isNotAnEpisode() {
        return notAnEpisode != null && notAnEpisode;
    }

    @Deprecated
    public void setNotAnEpisode(boolean notAnEpisode) {
        this.notAnEpisode = notAnEpisode;
    }

    public boolean isFindDeleted() {
        return findDeleted == null || findDeleted;
    }

    public boolean hasExcludedMids() {
        return has(excludedMids);
    }

    public boolean hasIds() {
        return has(ids);
    }

    public boolean hasSort() {
        return pager.getSort() != null;
    }

    private static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private static boolean has(Collection<?> collection) {
        return ! isEmpty(collection);
    }

    private <T> Collection<T> collection(Collection<T> col) {
        if(col == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableCollection(col);
        }
    }

    /**
     * @return The classes that are represented by the types in this form.
     * @since 8.0
     */
    public Collection<Class<? extends MediaObject>> getClasses() {
        return MediaType.getClasses(hasTypes() ? getTypes() : null);
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
