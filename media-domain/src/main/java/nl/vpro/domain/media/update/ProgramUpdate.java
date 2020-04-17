/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.util.IntegerVersion;


@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "programUpdateType", propOrder = {
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
    "scheduleEvents",
    "relations",
    "images",
    "asset",
    "episodeOf",
    "segments"
})
public final class ProgramUpdate extends MediaUpdate<Program> {

    protected SortedSet<@NotNull @Valid SegmentUpdate> segments;

    protected SortedSet<@NotNull @Valid MemberRefUpdate> episodeOf;

    protected  SortedSet<@NotNull @Valid ScheduleEventUpdate> scheduleEvents;


    private ProgramType programType;


    public ProgramUpdate() {
        super();
    }

    public ProgramUpdate(IntegerVersion version, Program program, OwnerType owner) {
        super(version, program, owner);
    }

    public static ProgramUpdate create() {
        return create(new Program());
    }

    public static ProgramUpdate create(Program program) {
        return create(null, program, OwnerType.BROADCASTER);
    }

    public static ProgramUpdate create(IntegerVersion version, Program program) {
        return create(version, program, OwnerType.BROADCASTER);
    }


    public static ProgramUpdate create(IntegerVersion version, Program program, OwnerType owner) {
        return new ProgramUpdate(version, program, owner);
    }

    public static ProgramUpdate create(Program program, OwnerType owner) {
        return create(null, program, owner);
    }

    public static ProgramUpdate create(MediaBuilder.ProgramBuilder builder) {
        return  create(builder, OwnerType.BROADCASTER);
    }

    public static ProgramUpdate create(MediaBuilder.ProgramBuilder builder, OwnerType owner) {
        return  create(null, builder.build(), owner);
    }

    @Override
    protected void fillFrom(Program mediaobject, OwnerType owner) {
        this.programType = mediaobject.getType();
        this.segments = toSet(mediaobject.getSegments(), s -> SegmentUpdate.createForParent(this, s, owner));
        this.episodeOf = toSet(mediaobject.getEpisodeOf(), MemberRefUpdate::create);
        this.scheduleEvents = toSet(mediaobject.getScheduleEvents(), (s) -> new ScheduleEventUpdate(this, s));


    }

    @Override
    protected Program newMedia() {
        return new Program();

    }

    @Override
    public Program fetch(OwnerType owner) {
        Program p  = super.fetch(owner);
        p.setType(programType);
        p.setSegments(toSet(segments, (s) -> {
                Segment result = s.fetch(owner);
                result.setMidRef(null);
                result.setParent(p);
                return result;
            })
        );

        p.setScheduleEvents(toSet(scheduleEvents, s -> {
            ScheduleEvent e = s.toScheduleEvent(owner);
            e.setParent(p);
            return e;
        }));
        // handled in  nl.vpro.domain.media.update.MediaUpdateServiceImpl.fetch
        // but this seems odd.
   /*     p.setEpisodeOf(toSet(episodeOf, s -> {
            MemberRef r = s.toMemberRef(owner);
            return r;
        }));*/
        return p;
    }

    @XmlAttribute
    @Override
    @NotNull
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

    @XmlElementWrapper(name = "scheduleEvents")
    @XmlElement(name = "scheduleEvent")
    @NonNull
    public SortedSet<ScheduleEventUpdate> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        return scheduleEvents;
    }

    public void setScheduleEvent(ScheduleEventUpdate... events) {
        this.scheduleEvents = new TreeSet<>(Arrays.asList(events));
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
