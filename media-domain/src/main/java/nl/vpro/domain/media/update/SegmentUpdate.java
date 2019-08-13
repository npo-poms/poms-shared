/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.SegmentType;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;

@XmlRootElement(name = "segment")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "segmentUpdateType", propOrder = {
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
    "avAttributes",
    "releaseYear",
    "duration",
    "persons",
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
    "start"
})
public final class SegmentUpdate extends MediaUpdate<Segment>
    implements Comparable<SegmentUpdate>, Child<ProgramUpdate> {


    private SegmentType segmentType;
    private java.time.Duration start;
    private String midRef;
    private ProgramUpdate parent;

    private SegmentUpdate() {

    }

    private SegmentUpdate(Segment segment, OwnerType ownerType) {
        super(segment, ownerType);
    }

    @Override
    protected void fillFrom(Segment mediaObject, OwnerType ownerType) {
        fillFromFor(mediaObject.getParent() == null ? null : new ProgramUpdate(mediaObject.getParent(), ownerType), mediaObject, ownerType);
    }



    //**
    protected void fillFromFor(ProgramUpdate parent, Segment mediaObject, OwnerType ownerType) {
        this.parent = parent;
        this.segmentType = mediaObject.getType();
        this.start = mediaObject.getStart();
        if (parent == null) {
            this.midRef = mediaObject.getMidRef();
        }
    }


    @Override
    public Segment fetch(OwnerType ownerType) {
        Segment p  = super.fetch(ownerType);
        p.setStart(start);
        p.setType(segmentType);
        p.setMidRef(midRef);
        p.setOwner(ownerType);
        return p;
    }


    public static SegmentUpdate create() {
        return new SegmentUpdate();
    }

     public static SegmentUpdate createForParent(ProgramUpdate parent, Segment segment, OwnerType ownerType) {
        SegmentUpdate segmentUpdate = new SegmentUpdate();
        segmentUpdate.fillFromMedia(segment, ownerType);
        segmentUpdate.fillFromFor(parent, segment, ownerType);
        return segmentUpdate;
    }

    public static <T extends MediaBuilder.AbstractSegmentBuilder<T>> SegmentUpdate create(MediaBuilder.AbstractSegmentBuilder<T> builder) {
        return new SegmentUpdate(builder.build(), OwnerType.BROADCASTER);
    }

    public static SegmentUpdate create(Segment segment, OwnerType ownerType) {
        return new SegmentUpdate(segment, ownerType);
    }

    public static SegmentUpdate createForProgram(Segment segment, OwnerType ownerType) {
        return new SegmentUpdate(segment, ownerType);
     }


    public static SegmentUpdate create(Segment segment) {
        return create(segment, OwnerType.BROADCASTER);
     }

    @Override
    protected Segment newMedia() {
        return new Segment();

    }

    @Override
    @XmlTransient
    public SegmentType getType() {
        return segmentType;
    }

    @Override
    protected String getUrnPrefix() {
        return SegmentType.URN_PREFIX;
    }


    public void setType(SegmentType type) {
        if (type == null) {
            type = SegmentType.SEGMENT;
        }
        this.segmentType = type;
    }


    @XmlAttribute(name = "type")
    protected SegmentType getTypeAttribute() {
        SegmentType type = getType();
        if (type == SegmentType.SEGMENT) {
            return null;
        }
        return type;

    }


    protected void setTypeAttribute(SegmentType type) {
        this.setType(type);
    }


    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE, required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    public java.time.Duration getStart() {
        return start;
    }

    public void setStart(java.time.Duration start) {
        this.start = start;
    }

    @XmlTransient
    public void setMidRef(String string) {
        this.midRef = string;
    }
    public String getMidRef() {
        return midRef;
    }

    @XmlAttribute(name = "midRef")
    public void setMidRefAttribute(String string) {
        setMidRef(string);
    }

    public String getMidRefAttribute() {
        if (parent != null) {
            return parent.getMid();
        }
        return midRef;
    }


    @Override
    public int compareTo(@NonNull SegmentUpdate segmentUpdate) {
        //return builder.build().compareTo(segmentUpdate.builder.build());
         if(super.equals(segmentUpdate)) {
            return 0;
        }

        if(this.start != null && segmentUpdate.start != null) {
            int compare = this.start.compareTo(segmentUpdate.getStart());
            if (compare != 0) {
                return compare;
            }
        }
        if(this.segmentType != null && segmentUpdate.segmentType  != null) {
            int compare = this.segmentType.compareTo(segmentUpdate.getType());
            if (compare != 0) {
                return compare;
            }
        }
        {
            int compare = this.getMainTitle().compareTo(segmentUpdate.getMainTitle());
            if (compare != 0) {
                return compare;
            }

        }

        if (this.getMid() != null && segmentUpdate.getMid() != null) {
            int compare = this.getMid().compareTo(segmentUpdate.getMid());
            if (compare != 0) {
                return compare;
            }
        }
        if (this.getId() != null && segmentUpdate.getId() != null) {
            int compare = this.getId().compareTo(segmentUpdate.getId());
            if (compare != 0) {
                return compare;
            }
        }
        return segmentUpdate.hashCode() - hashCode();
    }

    @Override
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        super.afterUnmarshal(unmarshaller, parent);
        if(parent instanceof ProgramUpdate) {
            this.parent = (ProgramUpdate) parent;
        }
    }


    @Override
    @XmlTransient
    public void setParent(ProgramUpdate mo) {
        this.parent = mo;

    }

    @Override
    public ProgramUpdate getParent() {
        return parent;

    }

    @Override
    public String getCorrelationId() {
        return getMidRefAttribute();
    }
}
