/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.ProgramType;
import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.util.TransformingSortedSet;


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


    private ProgramUpdateConfig updateConfig = new ProgramUpdateConfig();

    public ProgramUpdate() {
        super(MediaBuilder.program());
    }

    private ProgramUpdate(MediaBuilder.AbstractProgramBuilder builder) {
        super(builder);
    }

    private ProgramUpdate(MediaBuilder.AbstractProgramBuilder builder, OwnerType ownerType) {
        super(builder, ownerType);
    }

    private ProgramUpdate(Program program) {
        this(program, OwnerType.BROADCASTER);
    }


    private ProgramUpdate(Program program, OwnerType type) {
        super(MediaBuilder.program(program), type);
    }

    public static ProgramUpdate create() {
        return new ProgramUpdate(MediaBuilder.program());
    }

    public static ProgramUpdate create(MediaBuilder.AbstractProgramBuilder builder) {
        return new ProgramUpdate(builder);
    }

    public static ProgramUpdate create(MediaBuilder.AbstractProgramBuilder builder, OwnerType ownerType) {
        return new ProgramUpdate(builder, ownerType);
    }

    public static ProgramUpdate create(Program program) {
        return new ProgramUpdate(program);
    }

    public static ProgramUpdate create(ProgramType type) {
        ProgramUpdate result = create();
        result.setType(type);
        return result;
    }


    public static ProgramUpdate forOwner(Program program, OwnerType type) {
        return new ProgramUpdate(program, type);
    }

    public static ProgramUpdate forAllOwners(Program program) {
        return forOwner(program, null);
    }

    @Override
    public MediaBuilder.ProgramBuilder getBuilder() {
        return (MediaBuilder.ProgramBuilder) super.getBuilder();
    }

    @Override
    public ProgramUpdateConfig getConfig() {
        return updateConfig;
    }

    @Override
    public Program fetch() {
        if(notTransforming(segments)) {
            build().setSegments(segments.stream().map(MediaUpdate::fetch).collect(Collectors.toCollection(TreeSet::new)));
            segments = null;
        }
        if (notTransforming(episodeOf)) {
            build().setEpisodeOf(episodeOf.stream().map(this::toMemberRef).collect(Collectors.toCollection(TreeSet::new)));
            episodeOf = null;
        }
        return super.fetch();
    }

    @Override
    Program fetch(OwnerType owner) {
        if (segments != null) {
            build().setSegments(segments.stream().map(s -> s.fetch(owner)).collect(Collectors.toCollection(TreeSet::new)));
            segments = null;
        }
        return super.fetch(owner);
    }

    @Override
    Program fetch(ImageImporter importer, AssemblageConfig assemblage) {
        if (segments != null) {
            for (SegmentUpdate update : segments) {
                Segment segment = update.fetch(importer, assemblage);
                getBuilder().segments(segment);
            }
            segments = null;
        }
        return super.fetch(importer, assemblage); // super checks on download once!
    }

    @XmlAttribute
    @Override
    public ProgramType getType() {
        return builder.build().getType();
    }

    public void setType(ProgramType type) {
        getBuilder().type(type);
    }

    @XmlElement
    public SortedSet<MemberRefUpdate> getEpisodeOf() {
        if (episodeOf == null) {
            episodeOf = new TransformingSortedSet<>(build().getEpisodeOf(),
                memberRef -> new MemberRefUpdate(memberRef.getNumber(), memberRef.getMediaRef(), memberRef.isHighlighted()),
                this::toMemberRef
            );

        }
        return episodeOf;
    }

    public void setEpisodeOf(SortedSet<MemberRefUpdate> memberOf) {
        this.episodeOf = memberOf;
    }



    @XmlElementWrapper(name = "segments")
    @XmlElement(name = "segment")
    public SortedSet<SegmentUpdate> getSegments() {
        if(this.segments == null) {
            this.segments = new TransformingSortedSet<>(build().getSegments(),
                SegmentUpdate::create,
                MediaUpdate::fetch
            );
        }
        return this.segments;

    }

    public void setSegments(SortedSet<SegmentUpdate> segments) {
        this.segments = segments;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ProgramUpdate")
            .append("{program=")
            .append(builder.build())
            .append('}');
        return sb.toString();
    }


}
