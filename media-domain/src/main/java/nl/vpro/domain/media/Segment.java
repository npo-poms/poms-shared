package nl.vpro.domain.media;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.support.Duration;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.util.TimeUtils;
import nl.vpro.validation.SegmentValidation;
import nl.vpro.xml.bind.DateToDuration;

/**
 * A segment is a view on a program.
 */

@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "segment")
@XmlType(name = "segmentType", propOrder = {
    "start"
})
@JsonTypeName("segment")
@SegmentValidation
public class Segment extends MediaObject implements Comparable<Segment> {


    private static final long serialVersionUID = -868293795041160925L;

    @ManyToOne(targetEntity = Program.class, optional = false)
    protected Program parent;

    @Column(nullable = false)
    @NotNull(message = "start property is required")
    protected Date start;

    @Transient
    protected String urnRef;

    @Transient
    protected String midRef;

    public Segment() {
    }

    public Segment(Program program, String mid, Date start, Duration duration) {
        this.start = start;
        this.midRef = mid;
        this.duration = duration;
        avType = program.getAVType();
        program.addSegment(this);
    }

    public Segment(Program program) {
        this(program, program.getMid(), new Date(0), program.getDuration());
    }

    public Segment(Program program, Date start, Date duration) {
        this(program, program.getMid(), start, new Duration(duration));
    }

    public Segment(Program program, Date start, Duration duration) {
        this(program, program.getMid(), start, duration);
    }

    public Segment(AVType avType) {
        this.avType = avType;
    }

    public Segment(AVType avType, Date start) {
        this.avType = avType;
        this.start = start;
    }

    public Segment(AVType avType, Date start, Date duration) {
        this(avType, start, new Duration(duration));
    }

    public Segment(AVType avType, Date start, Duration duration) {
        this.avType = avType;
        this.start = start;
        this.duration = duration;
    }

    public Segment(Segment source) {
        this(source, source.parent);
    }

    public Segment(Segment source, Program parent) {
        super(source);
        this.start = source.start;
        this.midRef = source.midRef;
        this.urnRef = source.urnRef;
    }

    public static Segment copy(Segment source) {
        return copy(source, source.parent);
    }

    public static Segment copy(Segment source, Program parent) {
        if(source == null) {
            return null;
        }
        return new Segment(source);
    }

    @Override
    public boolean isActivation() {
        return getParent().isActivation() || super.isActivation();
    }

    @Override
    public boolean isDeactivation() {
        return getParent().isDeactivation() || super.isDeactivation();
    }

    @Override
    public boolean isPublishable() {
        if(!parent.isPublishable()) {
            return false;
        }

        return super.isPublishable();
    }

    @Override
    public boolean isRevocable() {
        if(super.isRevocable()) {
            return true;
        }

        return getParent().isRevocable();
    }

    @Override
    public boolean isMerged() {
        return (parent != null && parent.isMerged()) || super.isMerged();
    }

    public Program getParent() {
        return parent;
    }

    void setParent(Program parent) {
        if(parent == null) {
            throw new IllegalArgumentException();
        }
        this.parent = parent;
        this.sortDateValid = false;
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
            if (parent.getMid().equals(midRef)) {
                return;
            } else {
                throw new IllegalStateException("This segments program holds the midRef for this segment");
            }
        }

        this.midRef = midRef;
    }

    @Override
    protected String getUrnPrefix() {
        return SegmentType.URN_PREFIX;
    }

    @XmlJavaTypeAdapter(DateToDuration.class)
    @XmlElement(required = true)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerDate.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }


    @XmlTransient
    @JsonIgnore
    public void setStart(java.time.Duration start) {
        this.start = TimeUtils.asDate(start);
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
        return super.hasAncestor(ancestor) || parent != null ? parent.hasAncestor(ancestor) : false;
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
    public int compareTo(Segment o) {
        if(super.equals(o)) {
            return 0;
        }

        if(this.start != null && o.start != null) {
            int compare = this.start.compareTo(o.getStart());
            if (compare != 0) {
                return compare;
            }
        }
        {
            int compare = this.getMainTitle().compareTo(o.getMainTitle());
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
    @Override
    public SegmentType getType() {
        return SegmentType.SEGMENT;
    }


    public void setType(SegmentType segmentType) {
        if(segmentType == null) {
            throw new IllegalArgumentException("Setting null segment type is not allowed");
        }
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof Program) {
            this.parent = (Program)parent;
            this.urnRef = null;
        }
    }

    @Override
    public String toString() {
        return String.format("Segment{%s, title=\"%2$s\"} for %3$s}", this.mid, this.getMainTitle(), parent);
    }
}
