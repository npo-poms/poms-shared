/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.MemberRef;
import nl.vpro.domain.media.support.OwnerType;

/**
 * @see nl.vpro.domain.media.update
 * @see MemberRef
 */
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberRefUpdateType")
@XmlRootElement(name = "memberRef")
public class MemberRefUpdate implements Comparable<MemberRefUpdate> {

    @XmlAttribute
    @Getter
    private Integer position;

    @XmlAttribute(required = false)
    Boolean highlighted = false;

    @XmlValue
    @Getter
    private String mediaRef;



    public static MemberRefUpdate create(MemberRef m) {
        return new MemberRefUpdate(
            m.getNumber(),
            m.getMediaRef(),
            m.isHighlighted());
    }

    public MemberRefUpdate() {
    }

    public MemberRefUpdate(Integer position, String mediaRef) {
        this.position = position;
        this.mediaRef = mediaRef;
    }

    @lombok.Builder
    private MemberRefUpdate(
        Integer position,
        String mediaRef,
        Boolean highlighted) {
        this(position, mediaRef);
        this.highlighted = highlighted;
    }


    public Boolean isHighlighted() {
        return highlighted;
    }

    public MemberRef toMemberRef(OwnerType type) {
        return
            MemberRef.builder()
                .number(position)
                .highlighted(highlighted)
                .added(null)
                .mediaRef(mediaRef)
                .owner(type)
                .build();

    }

    @Override
    public int compareTo(@NonNull MemberRefUpdate memberRefUpdate) {
        if (position != null && memberRefUpdate.getPosition() != null) {
            int diff = getPosition() - memberRefUpdate.getPosition();
            if (diff != 0) {
                return diff;
            }
        }
        if (position == null) {
            return 1;
        }
        if (memberRefUpdate.getPosition() == null) {
            return -1;
        }
        return ObjectUtils.compare(memberRefUpdate.getMediaRef(), getMediaRef());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberRefUpdate that = (MemberRefUpdate) o;

        if (position != null ? !position.equals(that.position) : that.position != null) return false;
        if (highlighted != null ? !highlighted.equals(that.highlighted) : that.highlighted != null) return false;
        return mediaRef != null ? mediaRef.equals(that.mediaRef) : that.mediaRef == null;

    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (highlighted != null ? highlighted.hashCode() : 0);
        result = 31 * result + (mediaRef != null ? mediaRef.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return position + ":" + mediaRef + ((highlighted == null || ! highlighted) ? "" : ":highlighted");
    }
}
