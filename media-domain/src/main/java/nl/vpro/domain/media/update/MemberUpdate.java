/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.annotation.*;


/**
 * @see nl.vpro.domain.media.update
 * @see nl.vpro.domain.media.Member
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberUpdateType")
@XmlRootElement(name = "memberUpdate")
public class MemberUpdate {

    @XmlAttribute
    private Integer position;

    @XmlAttribute(required = true)
    Boolean highlighted = false;

    @XmlElement
    private MediaUpdate<?> mediaUpdate;

    public MemberUpdate() {
    }

    public MemberUpdate(Integer position, MediaUpdate<?> mediaUpdate) {
        this.position = position;
        this.mediaUpdate = mediaUpdate;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public MediaUpdate<?> getMediaUpdate() {
        return mediaUpdate;
    }


    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    public String toString() {
        return position + ":" + mediaUpdate;
    }
}
