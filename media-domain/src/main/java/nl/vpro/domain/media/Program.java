package nl.vpro.domain.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.time.Instant;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.*;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.user.*;

import static javax.persistence.CascadeType.MERGE;
import static nl.vpro.domain.TextualObjects.sorted;

/**
 * The main feature that distinguishes a Program from a generic media entity is its ability
 * to become an episode of other media entities. This association type is a functional
 * equivalent of the memberOf association, but complementary, and has its own representation
 * in XML or JSON.
 * <p/>
 * A program can have a {@link nl.vpro.domain.media.ProgramType} when it's a movie or strand
 * program. A strand programs has the ability to become an episode of other strand programs
 * as opposed to strand groups.
 * <p>
 * Another important distinction is that only programs may contain {@link Segment}s. Also, they themselves know of their segments,
 * and the XML and JSON representations on programs normally contain all their segments too.
 *
 * @author roekoe
 */
@Entity
@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "programType", propOrder = {
    "scheduleEvents",
    "episodeOf",
    "segments",
    "poProgTypeLegacy"

})
@JsonTypeName("program")
@Slf4j
public final class Program extends MediaObject {
    @Serial
    private static final long serialVersionUID = 6174884273805175998L;

    public static MediaBuilder.ProgramBuilder builder() {
        return MediaBuilder.program();
    }
    /**
     * Unset some default values, to ensure that roundtripping will result same object
     * @since 5.11
     */
    @JsonCreator
    static Program jsonCreator() {
        return builder().workflow(null).creationDate((Instant) null).build();
    }


    @OneToMany(mappedBy = "mediaObject", orphanRemoval = true, cascade={MERGE})
    @SortNatural
    // Caching doesn't work properly because ScheduleEventRepository may touch this
    // @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid Set<@NotNull ScheduleEvent> scheduleEvents;



    // DRS I found that the 'hardcoded' mediaobject alias in the filter below changes when
    // relational fields are added; I had to change the alias from mediaobjec_9 to mediaobjec_11
    // when I added field publicationRule below. Needs to be fixed, not sure how...
    @OneToMany(orphanRemoval = true)
    @JoinTable(
        name = "program_episodeof",
        inverseJoinColumns = @JoinColumn(name = "id")
    )
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    @SortNatural
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

    // TODO: These filters are horrible
    @FilterJoinTables({
        @FilterJoinTable(name = PUBLICATION_FILTER, condition =
            "((mediaobjec2_.publishstart is null or mediaobjec2_.publishstart < now())" +
                "and (mediaobjec2_.publishstop is null or mediaobjec2_.publishstop > now()))"),
        @FilterJoinTable(name = DELETED_FILTER, condition = "(mediaobjec2_.workflow NOT IN ('FOR_DELETION', 'DELETED') and (mediaobjec2_.mergedTo_id is null))")
    })
    Set<MemberRef> episodeOf = new TreeSet<>();

    @Size.List({@Size(max = 255), @Size(min = 1)})
    private String poProgType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "no program type given")
    private ProgramType type;

    @OneToMany(mappedBy = "parent", orphanRemoval = false) // no implicit orphan removal, the segment my be subject to 'stealing'.
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    //@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // TODO: These filters are horrible
    @Filters({
        @Filter(name = PUBLICATION_FILTER, condition =
            "((segments0_1_.publishstart is null or segments0_1_.publishstart < now())" +
                "and (segments0_1_.publishstop is null or segments0_1_.publishstop > now()))"),

        @Filter(name = DELETED_FILTER, condition = "(segments0_1_.workflow NOT IN ('MERGED', 'FOR_DELETION', 'DELETED') and (segments0_1_.mergedTo_id is null))")
    })

    private Set<Segment> segments;

    @XmlTransient
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private Boolean pdAuthorityImported;

    public Program() {

    }

    public Program(String mid, long id) {
        this(id);
        this.mid = mid;
    }


    public Program(String mid) {
        super();
        this.mid = mid;
    }


    public Program(long id) {
        super(id);
    }


    public Program(AVType avType, ProgramType type) {
        this.avType = avType;
        this.type = type;
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public Program(Program source) {
        super(source);
        source.getEpisodeOf().forEach(ref -> this.createEpisodeOf((Group)ref.getGroup(), ref.getNumber(), ref.getOwner()));
        source.getSegments().forEach(segment -> this.addSegment(Segment.copy(segment)));
        this.type = source.type;
        this.poProgType = source.poProgType;
        source.getScheduleEvents()
            .forEach(scheduleevent -> this.addScheduleEvent(ScheduleEvent.copy(scheduleevent, this)));
    }

    public static Program copy(Program source) {
        if(source == null) {
            return null;
        }
        return new Program(source);
    }

    public boolean hasScheduleEvents() {
        return scheduleEvents != null && !scheduleEvents.isEmpty();
    }

    @XmlElementWrapper(name = "scheduleEvents")
    @XmlElement(name = "scheduleEvent")
    @JsonProperty("scheduleEvents")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonManagedReference
    public SortedSet<ScheduleEvent> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        // return Collections.unmodifiableSortedSet(scheduleEvents); Would be
        // nice for hibernate, but jaxb gets confused (run ScheduleTest)
        return sorted(scheduleEvents);
    }

    public void setScheduleEvents(SortedSet<ScheduleEvent> scheduleEvents) {
        this.scheduleEvents = scheduleEvents;
        invalidateSortDate();
    }

    MediaObject addScheduleEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvent != null) {
            if (scheduleEvents == null) {
                scheduleEvents = new TreeSet<>();
            }
            boolean wasNew =  scheduleEvents.add(scheduleEvent);
            if (! wasNew) {
                log.debug("Didn't add {}", scheduleEvent);
            }
            invalidateSortDate();
        }
        return this;
    }

    boolean removeScheduleEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvents != null) {
            return scheduleEvents.remove(scheduleEvent);
        }
        return false;
    }


    private static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public Boolean isEpisodeOfLocked() {
        if(episodeOf != null) {
            for(MemberRef memberRef : episodeOf) {
                MediaObject owner = memberRef.getGroup();
                if(owner instanceof Group && ((Group)owner).isEpisodesLocked()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    void addAncestors(SortedSet<MediaObject> set) {
        super.addAncestors(set);
        if (isEpisode()) {
            for (MemberRef memberRef : episodeOf) {
                if (! memberRef.isVirtual()) {
                    final MediaObject reference = memberRef.getGroup();
                    if (reference != null && !set.contains(reference)) {
                        set.add(reference);
                        reference.addAncestors(set);

                    }
                }
            }
        }
    }

    @Override
    protected Set<MemberRef> getVirtualMemberRefs() {
        Set<MemberRef> result = super.getVirtualMemberRefs();
        if (episodeOf != null) {
            for (MemberRef memberRef : episodeOf) {
                if (memberRef.isVirtual()) {
                    result.add(memberRef);
                }

            }
        }
        return result;
    }

    @XmlElement
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonManagedReference
    public SortedSet<@NonNull MemberRef> getEpisodeOf() {
        if(this.episodeOf == null) {
            this.episodeOf = new TreeSet<>();
        }
        for (MemberRef r : episodeOf) {
            r.setRefType(MemberRefType.episodeOf);
        }
        return sorted(episodeOf);
    }

    public void setEpisodeOf(SortedSet<MemberRef> episodeOf) {
        this.episodeOf = episodeOf;
        for (MemberRef r : episodeOf) {
            r.setMember(this);
        }
    }

    public MemberRef findEpisodeOfRef(long refId) {
        for(MemberRef memberRef : episodeOf) {
            if(memberRef.getId().equals(refId)) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findEpisodeOfRef(MediaObject owner) {
        return MemberRefs.findRef(episodeOf, owner).orElse(null);
    }

    public MemberRef findEpisodeOfRef(MediaObject owner, Integer number) {
        return MemberRefs.findRef(episodeOf, owner, number).orElse(null);
    }

    public MemberRef findEpisodeOf(Long episodeRefId) {
        for(MemberRef episodeRef : episodeOf) {
            if(episodeRefId.equals(episodeRef.getId())) {
                return episodeRef;
            }
        }
        return null;
    }

    public boolean isEpisode() {
        return episodeOf != null && episodeOf.size() > 0;
    }

    public boolean isEpisodeOf(MediaObject owner) {
        return MemberRefs.isOf(episodeOf, owner);
    }

    @Override
    public boolean hasAncestor(MediaObject ancestor) {
        if(super.hasAncestor(ancestor)) {
            return true;
        }

        if(!isEpisode()) {
            return false;
        }

        for(MemberRef memberRef : episodeOf) {
            if (memberRef.getGroup() != null) {
                if (Objects.equals(memberRef.getGroup(), ancestor) || memberRef.getGroup().hasAncestor(ancestor)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        super.findAncestry(ancestor, ancestors);

        if(ancestors.isEmpty() && isEpisode()) {
            for(MemberRef memberRef : episodeOf) {
                if(Objects.equals(memberRef.getGroup(), ancestor)) {
                    ancestors.add(ancestor);
                    return;
                }
                if (memberRef.getGroup() != null) {
                    memberRef.getGroup().findAncestry(ancestor, ancestors);
                    if (!ancestors.isEmpty()) {
                        ancestors.add(memberRef.getGroup());
                        return;
                    }
                }
            }
        }
    }



    MemberRef createEpisodeOf(Group group, Integer episodeNr, OwnerType owner) throws CircularReferenceException {
        if(group == null) {
            throw new IllegalArgumentException("Must supply an owning group, not null.");
        }

        if(! ProgramType.EPISODES.contains(this.getType())) {
            throw new IllegalArgumentException(
                String.format("%1$s of type %2$s can not become an episode of %3$s with type %4$s (should be one of %5$s)", this, this.getType(), group, group.getType(), ProgramType.EPISODES));
        }

        if(! group.getType().canContainEpisodes()) {
            throw new IllegalArgumentException("Must supply a group type " + GroupType.EPISODE_CONTAINERS + " when adding episodes.");
        }

        if(group.hasAncestor(this)) {
            throw new CircularReferenceException(group, group.findAncestry(this));
        }

        MemberRef memberRef = new MemberRef(this, group, episodeNr, owner);

        if(episodeOf == null) {
            episodeOf = new TreeSet<>();
        }

        episodeOf.add(memberRef);

        return memberRef;
    }

    boolean removeEpisodeOf(MediaObject owner) {
        boolean success = false;
        if(episodeOf != null) {
            Iterator<MemberRef> it = episodeOf.iterator();

            while(it.hasNext()) {
                MemberRef memberRef = it.next();

                if(Objects.equals(memberRef.getGroup(), owner)) {
                    it.remove();
                    success = true;
                }
            }
        }
        return success;
    }

    boolean removeEpisodeOf(MemberRef memberRef) {
        if(episodeOf != null) {
            Iterator<MemberRef> it = episodeOf.iterator();

            while(it.hasNext()) {
                MemberRef existing = it.next();

                if(existing.equals(memberRef)) {
                    it.remove();
                    descendantOf = null;
                    return true;
                }
            }
        }
        return false;
    }

    public String getPoProgType() {
        return poProgType;
    }

    @XmlElement(name = "poProgType")
    public String getPoProgTypeLegacy() {
        return null;
    }

    public void setPoProgTypeLegacy(String poProgType) {
        this.poProgType = (poProgType == null || poProgType.length() < 255) ? poProgType : poProgType.substring(255);
    }

    @XmlAttribute(required = true)
    @Override
    public ProgramType getType() {
        return type;
    }

    @Override
    public void setMediaType(MediaType type) {
        setType((ProgramType) type.getSubType());
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

    @XmlElementWrapper(name = "segments")
    @XmlElement(name = "segment")
    @JsonProperty("segments")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NonNull
    public SortedSet<Segment> getSegments() {
        if(segments == null) {
            segments = new TreeSet<>();
        }
        return sorted(segments);
    }

    public void setSegments(SortedSet<Segment> segments) {
        this.segments = segments;
    }

    public Segment findSegment(Long id) {
        if(segments == null) {
            return null;
        }

        for(Segment segment : segments) {
            if(id.equals(segment.getId())) {
                return segment;
            }
        }

        return null;
    }
    Optional<Segment> findSegment(Segment segment) {
        return getSegments().stream().filter(existing -> existing.equals(segment)).findFirst();
    }

    public Program addSegment(Segment segment) {
        if(segment != null) {
            segment.setParent(this);
            if(isEmpty(segment.getBroadcasters()) && !isEmpty(broadcasters)) {
                for(Broadcaster broadcaster : broadcasters) {
                    segment.addBroadcaster(broadcaster);
                }
            }
            if(isEmpty(segment.getPortals()) && !isEmpty(getPortals())) {
                for(Portal portal : getPortals()) {
                    segment.addPortal(portal);
                }
            }
            if(isEmpty(segment.getThirdParties()) && !isEmpty(getThirdParties())) {
                for(ThirdParty thirdParty : getThirdParties()) {
                    segment.addThirdParty(thirdParty);
                }
            }

            if(segments == null) {
                segments = new TreeSet<>();
            }
            segments.add(segment);
        }
        return this;
    }

    public boolean deleteSegment(Segment segment) {
        if(segments == null) {
            return false;
        }
        return findSegment(segment).map((existing) -> {
            existing.setWorkflow(Workflow.FOR_DELETION);
            return true;
            }
        ).orElse(false);
    }

    @Override
    protected String getUrnPrefix() {
        return ProgramType.URN_PREFIX;
    }
}

