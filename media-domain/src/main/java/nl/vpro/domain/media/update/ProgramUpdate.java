/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.ProgramType;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.support.OwnerType;


@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "programUpdateType", propOrder = {
    "episodeOf",
    "segments"
})
public final class ProgramUpdate extends MediaUpdate<Program> {

    @Valid
    protected SortedSet<SegmentUpdate> segments;

    @Valid
    protected SortedSet<MemberRefUpdate> episodeOf;


    private ProgramType programType;


    public ProgramUpdate() {
        super();
    }

    public ProgramUpdate(Program program, OwnerType ownerType) {
        super(program, ownerType);
    }


    public static ProgramUpdate create() {
        return create(new Program());
    }



    public static ProgramUpdate create(Program program) {
        return create(program, OwnerType.BROADCASTER);
    }

    public static ProgramUpdate create(Program program, OwnerType type) {
        return new ProgramUpdate(program, type);
    }


    public static ProgramUpdate create(MediaBuilder.ProgramBuilder builder) {
        return  create(builder.build());
    }


    @Override
    protected void fillFrom(Program mediaobject, OwnerType ownerType) {
        this.programType = mediaobject.getType();
        this.segments = toSet(mediaobject.getSegments(), s -> SegmentUpdate.createForParent(this, s, ownerType));
        this.episodeOf = toSet(mediaobject.getEpisodeOf(), MemberRefUpdate::create);
    }

    @Override
    protected Program newMedia() {
        return new Program();

    }

    @Override
    public Program fetch(OwnerType ownerType) {
        Program p  = super.fetch(ownerType);
        p.setType(programType);
        p.setSegments(toSet(segments, (s) -> {
                Segment result = s.fetch(ownerType);
                result.setMidRef(null);
                result.setParent(p);
                return result;
            })
        );
        return p;
    }



    @XmlAttribute
    @Override
    public ProgramType getType() {
        return programType;
    }

    @Override
    protected String getUrnPrefix() {
        return ProgramType.URN_PREFIX;

    }

    public void setType(ProgramType type) {
        this.programType = type;
    }

    @XmlElement
    public SortedSet<MemberRefUpdate> getEpisodeOf() {
        if (episodeOf == null) {
            episodeOf = new TreeSet<>();
        }
        return episodeOf;
    }

    public void setEpisodeOf(SortedSet<MemberRefUpdate> memberOf) {
        this.episodeOf = memberOf;
    }



    @XmlElementWrapper(name = "segments")
    @XmlElement(name = "segment")
    public SortedSet<SegmentUpdate> getSegments() {
        if (this.segments == null) {
            this.segments = new TreeSet<>();
        }
        return this.segments;

    }

    public void setSegments(SortedSet<SegmentUpdate> segments) {
        this.segments = segments;
    }





}
