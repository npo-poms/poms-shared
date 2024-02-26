/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.*;
import java.util.Objects;

import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonTypeName;

import nl.vpro.domain.Child;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.util.IntegerVersion;
import nl.vpro.validation.WarningValidatorGroup;
import nl.vpro.xml.bind.DurationXmlAdapter;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @see nl.vpro.domain.media.update
 * @see nl.vpro.domain.media.Segment
 */
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
    "start"
})
@SegmentUpdate.Valid
@Slf4j
@JsonTypeName("segmentUpdate")
public final class SegmentUpdate extends MediaUpdate<Segment>
    implements Comparable<SegmentUpdate>, Child<ProgramUpdate> {

    private SegmentType segmentType;

    private java.time.Duration start;

    private String midRef;
    private ProgramUpdate parent;

    public SegmentUpdate() {

    }

    private SegmentUpdate(IntegerVersion version, Segment segment, OwnerType ownerType) {
        super(version, segment, ownerType);
    }

    @Override
    protected void fillFrom(Segment mediaObject, OwnerType ownerType) {
        fillFromFor(mediaObject.getParent() == null ? null : new ProgramUpdate(version, mediaObject.getParent(), ownerType), mediaObject);
    }

    private void fillFromFor(
        ProgramUpdate parent,
        Segment mediaObject) {
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
        segmentUpdate.setVersion(parent.getVersion());
        segmentUpdate.fillFromMedia(segment, ownerType);
        segmentUpdate.fillFromFor(parent, segment);
        return segmentUpdate;
    }

    public static <T extends MediaBuilder.AbstractSegmentBuilder<T>> SegmentUpdate create(MediaBuilder.AbstractSegmentBuilder<T> builder) {
        return new SegmentUpdate(null, builder.build(), OwnerType.BROADCASTER);
    }

    public static SegmentUpdate create(Segment segment, OwnerType ownerType) {
        return new SegmentUpdate(null, segment, ownerType);
    }

    public static SegmentUpdate create(IntegerVersion version, Segment segment, OwnerType ownerType) {
        return new SegmentUpdate(version, segment, ownerType);
     }


    public static SegmentUpdate create(IntegerVersion version, Segment segment) {
        return new SegmentUpdate(version, segment, OwnerType.BROADCASTER);
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
        return segmentType == null ? SegmentType.SEGMENT : segmentType;
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
    private SegmentType getTypeAttribute() {
        SegmentType type = getType();
        if (type == SegmentType.SEGMENT) {
            return null;
        }
        return type;

    }


    private void setTypeAttribute(SegmentType type) {
        this.setType(type);
    }


    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE, required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @NotNull(groups = {WarningValidatorGroup.class})
    public java.time.Duration getStart() {
        return start;
    }

    public void setStart(java.time.Duration start) {
        this.start = start;
    }


    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE, required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Override
    public java.time.Duration getDuration() {
        return super.getDuration();
    }


    /**
     * The mid of the parent program. If the segment is sent as a standalone object, this can also be a crid.
     */
    @XmlTransient
    public void setMidRef(String string) {
        this.midRef = string;
    }
    public String getMidRef() {
        return midRef;
    }

    @XmlAttribute(name = "midRef")
    void setMidRefAttribute(String string) {
        setMidRef(string);
    }

    String getMidRefAttribute() {
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

    @XmlTransient
    public boolean isStandalone() {
        return parent == null;
    }

    @Override
    public Correlation getCorrelation() {
        return Correlation.mid(getMidRefAttribute());
    }
    @Target({TYPE_USE})
    @Retention(RUNTIME)
    @Constraint(validatedBy = Validator.class)
    @Documented
    public @interface Valid {
        String message() default "{nl.vpro.constraints.SegmentUpdate}";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class Validator implements ConstraintValidator<Valid, SegmentUpdate> {

        @Override
        public boolean isValid(SegmentUpdate value, ConstraintValidatorContext context) {
            if (! value.fromXml) {
                log.debug("This object was not read in from XML. It cannot be incorrect according to the rules below?");
                return true;
            }
            if (value.isStandalone()) {
                if (StringUtils.isBlank(value.midRef)) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode("midRef")
                        .addConstraintViolation();

                    log.info("{}", context.getDefaultConstraintMessageTemplate());
                    return false;
                }
            } else {
                if (StringUtils.isNotBlank(value.midRef)) {
                    if (! Objects.equals(value.midRef, value.parent.getMid())){
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                            .addPropertyNode("parent")
                            .addConstraintViolation();
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
