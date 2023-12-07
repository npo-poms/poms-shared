/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.Instant;
import java.util.TreeSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.OwnerType;



/**
 * A group represents a collection of other {@link MediaObject}s. They may contain similar meta data, but their main goal is to work
 * as a container. It may e.g. represent a {@link GroupType#SERIES}, {@link GroupType#SEASON}, {@link GroupType#ALBUM} or {@link GroupType#PLAYLIST}
 *
 */
@Entity
@Table(name = "group_table")
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "groupType", propOrder = {
    "poSequenceInformation"
})
@JsonTypeName("group")
public final class Group extends MediaObject {

    @Serial
    private static final long serialVersionUID = 1L;


    public static MediaBuilder.GroupBuilder builder() {
        return MediaBuilder.group();
    }

    /**
     * Unset some default values, to ensure that roundtripping will result same object
     * @since 5.11
     */
    @JsonCreator
    static Group jsonCreator() {
        return builder().workflow(null).creationDate((Instant) null).build();
    }


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private GroupType type;

    @Column(nullable = false)
    @NotNull
    private Boolean episodesLocked = false;

    @Column(nullable = false)
    @NotNull Boolean isOrdered = true;


    /**
     * Not persistent, never filled? What is this, should it not be dropped?
     */
    @XmlAttribute
    @Getter
    @Setter
    private Long defaultElement;

    @Column
    @XmlElement
    @Getter
    @Setter
    private String poSequenceInformation;

    public Group() {
    }

    public Group(long id) {
        super(id);
    }

    public Group(GroupType type) {
        this(null, type, true);
    }

    public Group(GroupType type, boolean isOrdered) {
        this(null, type, isOrdered);
    }

    public Group(AVType avType, GroupType type) {
        this(avType, type, true);
    }

    public Group(AVType avType, GroupType type, boolean isOrdered) {
        setAVType(avType);
        setType(type);
        setOrdered(isOrdered);
    }

    public Group(Group source) {
        super(source);
        this.type = source.type;
        this.isOrdered = source.isOrdered;
        this.episodesLocked = source.episodesLocked;
        this.defaultElement = source.defaultElement;
        this.poSequenceInformation = source.poSequenceInformation;
    }

    public static Group copy(Group source) {
        if(source == null) {
            return null;
        }
        return new Group(source);
    }

    MemberRef createMember(MediaObject member, OwnerType owner) throws CircularReferenceException {
        if(this.isOrdered) {
            throw new IllegalArgumentException("Can not add a member to an ordered group without supplying an ordering number.");
        }

        if(member.equals(this)) {
            throw CircularReferenceException.self(member, this, findAncestry(member));
        }
        if (member.hasDescendant(this)) {
            throw new CircularReferenceException(member, this, findAncestry(member));
        }

        if(member.memberOf == null) {
            member.memberOf = new TreeSet<>();
        }

        MemberRef memberRef = new MemberRef(member, this, null, owner);
        member.memberOf.add(memberRef);
        return memberRef;
    }

    @Override
    MemberRef createMember(
        @NonNull MediaObject member,
        Integer number,
        OwnerType owner) throws CircularReferenceException {
        if(number == null) {
            return createMember(member, owner);
        }

        return super.createMember(member, number, owner);
    }

    MemberRef createEpisode(Program episode, Integer episodeNumber, OwnerType owner) {
        return episode.createEpisodeOf(this, episodeNumber, owner);
    }

    @Override
    protected String getUrnPrefix() {
        return GroupType.URN_PREFIX;
    }

    @Override // to make it public
    public void setSortInstant(Instant date) {
        super.setSortInstant(date);
    }

    @XmlAttribute(required = true)
    @Override
    public GroupType getType() {
        return type;
    }

    @Override
    public void setMediaType(MediaType type) {
        setType((GroupType) type.getSubType());
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public boolean isEpisodesLocked() {
        return episodesLocked != null ? episodesLocked : false;
    }

    public void setEpisodesLocked(boolean episodesLocked) {
        this.episodesLocked = episodesLocked;
    }

    @XmlAttribute(name = "isOrdered", required = true)
    @JsonProperty("isOrdered")
    public Boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(Boolean ordered) {
        isOrdered = ordered;
    }

    public String getPoSeriesID() {
        return getMid();
    }

    public Group setPoSeriesID(String poSeriesID) {
        setMid(poSeriesID);
        return this;
    }

}
