/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.ObjectUtils;

import nl.vpro.domain.media.MemberRef;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberRefUpdateType")
@XmlRootElement(name = "memberRef")
public class MemberRefUpdate implements Comparable<MemberRefUpdate> {

    @XmlAttribute
    private Integer position;

    @XmlAttribute(required = true)
    Boolean highlighted = false;

    @XmlValue
    private String mediaRef;

    public static MemberRefUpdate create(MemberRef m) {
        return new MemberRefUpdate(m.getNumber(), m.getMediaRef(), m.isHighlighted());
    }

    public MemberRefUpdate() {
    }

    public MemberRefUpdate(Integer position, String mediaRef) {
        this.position = position;
        this.mediaRef = mediaRef;
    }

    @lombok.Builder
    public MemberRefUpdate(Integer position, String mediaRef, boolean highlighted) {
        this(position, mediaRef);
        this.highlighted = highlighted;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getMediaRef() {
        return mediaRef;
    }

    public void setMediaRef(String mediaRef) {
        this.mediaRef = mediaRef;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }

    public MemberRef toMemberRef() {
        MemberRef mr = new MemberRef(mediaRef, position);
        mr.setHighlighted(highlighted);
        mr.setAdded(null);
        return mr;
    }

    @Override
    public int compareTo(MemberRefUpdate memberRefUpdate) {
        if(getPosition() == null) {
            return -1;
        } else if (memberRefUpdate.getPosition() == null) {
            return 1;
        }
        int diff = memberRefUpdate.getPosition() - getPosition();
        if (diff != 0) {
            return diff;
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
}
