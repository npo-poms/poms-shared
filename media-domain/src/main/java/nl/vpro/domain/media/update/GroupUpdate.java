/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Setter;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.util.IntegerVersion;

/**
 * @see Group
 */
@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "groupUpdateType",
    propOrder = {
        "crids",
        "broadcasters",
        "portals",
        "portalRestrictions",
        "geoRestrictions",
        "titles",
        "descriptions",
        "tags",
        "countries",
        "languages",
        "genres",
        "intentions",
        "targetGroups",
        "geoLocations",
        "topics",
        "avAttributes",
        "releaseYear",
        "duration",
        "credits",
        "memberOf",
        "ageRating",
        "contentRatings",
        "email",
        "websites",
        "twitterrefs",
        "predictions",
        "locations",
        "relations",
        "images",
        "asset",
        "poSeriesID"
    })
@JsonTypeName("groupUpdate")
public final class GroupUpdate extends MediaUpdate<Group> {


    private GroupType groupType;

    @Setter
    private Boolean ordered = true;


    public GroupUpdate() {
        super();
    }


    private GroupUpdate(IntegerVersion version, Group group, OwnerType ownerType) {
        super(version, group, ownerType);
    }

    public static GroupUpdate create(IntegerVersion version, Group group, OwnerType ownerType) {
        return new GroupUpdate(version, group, ownerType);
    }


    public static GroupUpdate create(IntegerVersion version, Group group) {
        return create(version, group, OwnerType.BROADCASTER);
    }


    public static GroupUpdate create(Group group, OwnerType ownerType) {
        return create(null, group, ownerType);
    }

    public static GroupUpdate create(Group group) {
        return create(group, OwnerType.BROADCASTER);
    }


    public static GroupUpdate create() {
        return new GroupUpdate();
    }

    @Override
    protected void fillFrom(Group mediaObject, OwnerType ownerType) {
        this.groupType = mediaObject.getType();
        this.ordered = mediaObject.isOrdered();
    }


    @Override
    public Group fetch(OwnerType ownerType) {
        Group group = super.fetch(ownerType);
        group.setType(groupType);
        group.setOrdered(ordered);
        return group;

    }

    @Override
    protected Group newMedia() {
        return new Group();

    }

    @XmlAttribute(required = true)
    @Override
    public GroupType getType() {
        return groupType;
    }

    @Override
    protected String getUrnPrefix() {
        return GroupType.URN_PREFIX;

    }

    public void setType(GroupType type) {
        this.groupType = type;
    }

    @XmlAttribute
    public Boolean isOrdered() {
        return ordered;
    }


    @XmlElement
    public String getPoSeriesID() {
        return getMid();
    }

    public void setPoSeriesID(String poSeriesID) {
        setMid(poSeriesID);
    }
}
