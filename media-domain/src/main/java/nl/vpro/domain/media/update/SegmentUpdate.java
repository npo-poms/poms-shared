/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.Date;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.SegmentType;
import nl.vpro.xml.bind.DateToDuration;

@XmlRootElement(name = "segment")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "segmentUpdateType", propOrder = {
        "start"
        })
public final class SegmentUpdate extends MediaUpdate<Segment> implements Comparable<SegmentUpdate> {

    private SegmentUpdateConfig updateConfig = new SegmentUpdateConfig();

    private SegmentUpdate() {
        this(MediaBuilder.segment());
    }

    private SegmentUpdate(MediaBuilder.SegmentBuilder builder) {
        super(builder);
    }

    private SegmentUpdate(Segment segment) {
        super(MediaBuilder.segment(segment));
    }


    public static SegmentUpdate create() {
        return new SegmentUpdate(MediaBuilder.segment());
    }

    public static SegmentUpdate create(MediaBuilder.SegmentBuilder builder) {
        return new SegmentUpdate(builder);
    }

    public static SegmentUpdate create(Segment segment) {
        return new SegmentUpdate(segment);
    }

    @Override
    public MediaBuilder.SegmentBuilder getBuilder() {
        return (MediaBuilder.SegmentBuilder) super.getBuilder();
    }

    @Override
    public SegmentUpdateConfig getConfig() {
        return updateConfig;
    }

    //@XmlAttribute
    @Override
    public SegmentType getType() {
        return builder.build().getType();
    }


    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE, required = true)
	@XmlJavaTypeAdapter(DateToDuration.class)
    public Date getStart() {
        return builder.build().getStart();
    }

    public void setStart(Date start) {
        getBuilder().start(start);
    }
    @XmlAttribute
    public void setMidRef(String string) {
        getBuilder().midRef(string);
    }
    public String getMidRef() {
        Segment built = builder.build();
        return built == null ? null : built.getMidRef();
    }


    @Override
    public int compareTo(SegmentUpdate segmentUpdate) {
        return builder.build().compareTo(segmentUpdate.builder.build());
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent != null && parent instanceof ProgramUpdate) {
            ((MediaBuilder.SegmentBuilder)builder).parent((ProgramUpdate) parent);
        }
    }


}
