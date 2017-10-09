/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.annotation.*;

import nl.vpro.VersionService;
import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.GroupType;
import nl.vpro.domain.media.MediaBuilder;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "groupUpdateType",
    propOrder = {
        "poSeriesID"
        })
public final class GroupUpdate extends MediaUpdate<Group> {

    private GroupUpdateConfig updateConfig = new GroupUpdateConfig();

    private GroupUpdate() {
        this(MediaBuilder.group(), null);
    }

    private GroupUpdate(MediaBuilder.GroupBuilder builder, Float version) {
        super(builder, version);
    }

    private GroupUpdate(Group group, Float version) {
        super(MediaBuilder.group(group), version);
    }

    public static GroupUpdate create() {
        return new GroupUpdate(MediaBuilder.group(), VersionService.floatVersion());
    }

    public static GroupUpdate create(MediaBuilder.AbstractGroupBuilder<?> builder) {
        return new GroupUpdate(builder.build(), VersionService.floatVersion());
    }

    public static GroupUpdate create(Group group) {
        return new GroupUpdate(group, VersionService.floatVersion());
    }

    @Override
    public MediaBuilder.GroupBuilder getBuilder() {
        return (MediaBuilder.GroupBuilder) super.getBuilder();
    }

    @Override
    public GroupUpdateConfig getConfig() {
        return updateConfig;
    }

    @XmlAttribute
    @Override
    public GroupType getType() {
        return builder.build().getType();
    }

    public void setType(GroupType type) {
        getBuilder().type(type);
    }

    @XmlAttribute
    public Boolean isOrdered() {
        return builder.build().isOrdered();
    }

    public void setOrdered(Boolean ordered) {
        getBuilder().ordered(ordered);
    }


    @XmlElement
    public String getPoSeriesID() {
        return builder.build().getMid();
    }

    public void setPoSeriesID(String poSeriesID) {
        getBuilder().poSeriesID(poSeriesID);
    }
}
