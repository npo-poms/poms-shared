package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.Instant;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.media.support.*;
import nl.vpro.jackson2.Views;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.validation.SegmentValidation;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
 * A segment is a view on a program, representing a part of if starting at a given {@link #getStart()} after the beginning of the program (and with a shorter {@link #getDuration()}. It cannot exist alone, and always has a {@link #getParent()}, which always is a {@link Program}.
 */

@Entity
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "segment")
@XmlType(name = "segmentType", propOrder = {
    "segmentOf",
    "start"
})
@JsonTypeName("segment")
@SegmentValidation
public final class Segment extends MediaObject implements Comparable<Segment>, Child<Program>, MutableOwnable {

    @Serial
    private static final long serialVersionUID = -868293795041160925L;

    static boolean defaultCorrelation = false;

    public static MediaBuilder.SegmentBuilder builder() {
        return MediaBuilder.segment();
    }
    /**
     * Unset some default values, to ensure that roundtripping will result same object
     * @since 5.11
     */
    @JsonCreator
    static Segment jsonCreator() {
        return builder().workflow(null).creationDate((Instant) null).build();
    }

    @ManyToOne(targetEntity = Program.class, optional = false, fetch = FetchType.LAZY)
    Program parent;

    @Setter
    @Column(nullable = false)
    @NotNull(message = "start property is required")
    private java.time.Duration start;


    @Transient
    private String urnRef;

    @Transient
    private String midRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "no segment type given")
    private SegmentType type = SegmentType.SEGMENT;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;

    @Setter
    @Transient
    private RecursiveMemberRef segmentOf;

    public Segment() {
    }

    public Segment(@NonNull Program program, String midRef, java.time.Duration start, AuthorizedDuration duration) {
        this.start = start;
        this.duration = duration;
        avType = program.getAVType();
        program.addSegment(this);
        this.midRef = midRef == null ? program.getMid() : midRef;
    }

    public Segment(Program program) {
        this(program, program.getMid(), java.time.Duration.ZERO, program.getDuration());
    }

    public Segment(Program program, java.time.Duration start, java.time.Duration duration) {
        this(program, program.getMid(), start, new AuthorizedDuration(duration));
    }

    public Segment(Program program, java.time.Duration start, AuthorizedDuration duration) {
        this(program, program.getMid(), start, duration);
    }

    public Segment(String mid, Program program, java.time.Duration start, AuthorizedDuration duration) {
        this(program, program.getMid(), start, duration);
        this.mid = mid;
    }

    public Segment(AVType avType) {
        this.avType = avType;
    }

    public Segment(AVType avType, java.time.Duration start) {
        this.avType = avType;
        this.start = start;
    }

    public Segment(AVType avType, java.time.Duration start, java.time.Duration duration) {
        this(avType, start, new AuthorizedDuration(duration));
    }

    public Segment(AVType avType, java.time.Duration start, AuthorizedDuration duration) {
        this.avType = avType;
        this.start = start;
        this.duration = duration;
    }

    public Segment(Segment source) {
        super(source);
        this.start = source.start;
        this.midRef = source.midRef;
        this.urnRef = source.urnRef;
    }

    public static Segment copy(Segment source) {
        if (source == null) {
            return null;
        }
        return new Segment(source);
    }


    @Override
    public boolean isActivation(Instant now) {
        return getParent().isActivation(now) || super.isActivation(now);
    }

    @Override
    public boolean isDeactivation(Instant now) {
        return getParent().isDeactivation(now) || super.isDeactivation(now);
    }

    @Override
    public boolean isRevocable(Instant now) {
        if(super.isRevocable(now)) {
            return true;
        }

        return getParent().isRevocable(now);
    }

    /**
     * A segment is considered merged if its parent is.
     * <p >
     * Note that generally it won't have  {@link MediaObject#getMergedTo()}
     * @see MediaObject#isMerged()
     */
    @Override
    public boolean isMerged() {
        return (parent != null && parent.isMerged()) || super.isMerged();
    }

    /**
     * If a segment is member of a merged {@link Program} then its workflow will become {@link Workflow#MERGED}, to indicate that. But this will in all cases be considered {@link MediaObject#isDeleted()}
     * <p>
     * E.g. if such on object is requested from the API it should result 404.
     */
    @Override
    public boolean isDeleted() {
        return super.isDeleted() || isMerged();
    }

    /**
     * Returns the parent {@link Program} of this segment. Not that this does not work directly after a simple unmarshall of
     * an individual segment because the full program object simply is not available then.
     * <p>
     * Use {@link #getMidRef()} for the mid, and obtain it separately.
     */
    @Override
    public Program getParent() {
        return parent;
    }

    @Override
    public void setParent(Program parent) {
        if(parent == null && this.parent != null) {
            throw new IllegalArgumentException("Cannot set parent to null");
        }
        this.parent = parent;
        invalidateSortDate();
        this.midRef = null;
    }

    @XmlAttribute(required = true)
    public String getUrnRef() {
        if(parent != null) {
            return parent.getUrn();
        }

        return urnRef;
    }

    public void setUrnRef(String urnRef) {
        if(parent != null) {
            throw new IllegalStateException("This segments program holds the urnRef for this segment");
        }

        this.urnRef = urnRef;
    }

    /**
     * @since 1.9
     */
    @XmlAttribute(required = true)
    public String getMidRef() {
        if(parent != null) {
            return parent.getMid();
        }

        return midRef;
    }

    /**
     * @since 1.9
     */
    public void setMidRef(String midRef) {
        if(parent != null) {
            if (midRef != null) {
                if (Objects.equals(parent.getMid(), midRef)) {
                    return;
                } else {
                    throw new IllegalStateException("This segments program holds the midRef for this segment");
                }
            }
            if (segmentOf != null) {
                segmentOf.setMidRef(midRef);
            }
        }
        this.midRef = midRef;
    }

    @Override
    protected String getUrnPrefix() {
        return SegmentType.URN_PREFIX;
    }

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    public java.time.@NonNull Duration getStart() {
        return start;
    }

    @Override
    public SortedSet<MediaObject> getAncestors() {
        final SortedSet<MediaObject> ancestors = super.getAncestors();

        if(parent != null) {
            ancestors.add(parent);
            ancestors.addAll(parent.getAncestors());
        }

        return ancestors;
    }

    @Override
    public boolean hasAncestor(MediaObject ancestor) {
        return (super.hasAncestor(ancestor) || parent != null) && parent.hasAncestor(ancestor);
    }

    @Override
    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        super.findAncestry(ancestor, ancestors);

        if(ancestors.isEmpty() && parent != null) {
            parent.findAncestry(ancestor, ancestors);
            ancestors.add(parent);
        }
    }

    @Override
    public int compareTo(@NonNull Segment o) {
        if(super.equals(o)) {
            return 0;
        }

        if(this.start != null && o.start != null) {
            int compare = this.start.compareTo(o.getStart());
            if (compare != 0) {
                return compare;
            }
        }
        if(this.type != null && o.type != null) {
            int compare = this.type.compareTo(o.getType());
            if (compare != 0) {
                return compare;
            }
        }
        {
            int compare = Objects.compare(this.getMainTitle(), o.getMainTitle(), Comparator.nullsLast(Comparator.naturalOrder()));
            if (compare != 0) {
                return compare;
            }

        }

        if (this.getMid() != null && o.getMid() != null) {
            int compare = this.getMid().compareTo(o.getMid());
            if (compare != 0) {
                return compare;
            }
        }
        if (this.getId() != null && o.getId() != null) {
            int compare = this.getId().compareTo(o.getId());
            if (compare != 0) {
                return compare;
            }
        }
        return o.hashCode() - hashCode();
    }



    @XmlAttribute(required = true)
    @NotNull
    @Override
    public SegmentType getType() {
        return type;
    }

    @Override
    public void setMediaType(MediaType type) {
        setType(type == null ? null : (SegmentType) type.getSubType());
    }

    public void setType(SegmentType segmentType) {
        if(segmentType == null) {
            segmentType = SegmentType.SEGMENT;
        }
        this.type = segmentType;
    }


    @Override
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        super.afterUnmarshal(unmarshaller, parent);
        if(parent instanceof Program program) {
            this.parent = program;
            this.urnRef = null;
            this.midRef = null;
        }
    }

    /**
     * The correlation id of a segment currently is the correlation id of its _parent_.
     * <p>
     * I forgot why this is important, but I think it may give problems when a segment is converted to a clip and/or vice versa.
     * <p>
     * For 7.1 release we'll leave it
     */
    @Override
    public Correlation calcCorrelation() {
        if (defaultCorrelation) {
            return super.calcCorrelation();
        } else {
            if (parent != null) {
                return parent.getCorrelation();
            }
            String midRef = getMidRef();
            if (midRef != null) {
                return Correlation.mid(midRef);
            } else {
                return super.calcCorrelation();
            }
        }
    }

    @XmlElement(name = "segmentOf")
    @JsonView(Views.Forward.class)
    public RecursiveMemberRef getSegmentOf() {
        if (segmentOf == null) {
            if (parent != null) {
                segmentOf = RecursiveMemberRef.ofSegment(this);
            } else if (getMidRef() != null) {
                MediaType type = getDescendantOf().stream().filter(m -> m.midRef.equals(getMidRef())).map(DescendantRef::getType).findFirst().orElse(MediaType.PROGRAM);
                segmentOf = RecursiveMemberRef.builder()
                    .childMid(getMid())
                    .parentMid(getMidRef())
                    .parentType(type)
                    .build();
            }
        } else {
            if (segmentOf.getChildMid() == null) { // this is not in the serialization
                segmentOf.setChildMid(getMid());
            }
        }
        return segmentOf;
    }

}
