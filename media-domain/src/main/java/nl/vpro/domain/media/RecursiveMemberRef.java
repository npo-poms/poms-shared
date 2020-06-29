package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 5.13
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "recursiveMemberRef",
        propOrder = {"memberOf", "episodeOf", "segmentOf"})
@JsonPropertyOrder({
        "midRef",
        "urnRef",
        "type",
        "memberOf",
        "episodeOf"
})
@Setter
public class RecursiveMemberRef implements Serializable, RecursiveParentChildRelation, Comparable<RecursiveMemberRef> {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Getter
    protected String midRef;

    @Getter
    protected String childMid;

    @XmlAttribute
    @Getter
    protected MediaType type;


    @XmlElement(name = "memberOf")
    @Setter
    @Getter
    protected SortedSet<RecursiveMemberRef> memberOf;

    @XmlElement(name = "episodeOf")
    @Setter
    @Getter
    protected SortedSet<RecursiveMemberRef> episodeOf;

    @Getter
    @XmlElement(name = "segmentOf")
    protected RecursiveMemberRef segmentOf;

    @XmlAttribute
	@Getter
	Integer index;

	@XmlAttribute
	Boolean highlighted;



    public RecursiveMemberRef() {
    }

    @lombok.Builder(builderClassName = "Builder")
    private RecursiveMemberRef(
        @NonNull String childMid,
        @NonNull String parentMid,
        @NonNull MediaType parentType,
        Integer index,
        Boolean highlighted,
        SortedSet<RecursiveMemberRef> memberOf,
        SortedSet<RecursiveMemberRef> episodeOf,
        RecursiveMemberRef segmentOf
    ) {
        this.childMid = childMid;
        this.midRef = parentMid;
        this.type = parentType;
        this.index = index;
        this.highlighted = highlighted == null || ! highlighted ? null : Boolean.TRUE;
        this.memberOf = memberOf;
        this.episodeOf = episodeOf;
        this.segmentOf = segmentOf;
    }

    public static RecursiveMemberRef.Builder builderOf(String childMid, MediaObject parent) {
        return RecursiveMemberRef.builder()
            .childMid(childMid)
            .parentMid(parent.getMid())
            .parentType(parent.getMediaType())
            .memberOf(of(parent.getMemberOf()))
            .episodeOf(parent instanceof Program ? of(((Program) parent).getEpisodeOf()) : null)
            .segmentOf(parent instanceof Segment ? of(((Segment) parent)): null)
            ;
    }

    public static RecursiveMemberRef of(MemberRef  ref) {
        return builderOf(ref.getChildMid(), ref.getParent())
            .index(ref.getNumber())
            .highlighted(ref.isHighlighted())
            .build();
    }

    public static SortedSet<RecursiveMemberRef> of(Set<MemberRef> ref) {
        if (ref == null) {
            return null;
        }
        return ref
            .stream()
            .map(RecursiveMemberRef::of)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    public static RecursiveMemberRef of(Segment segment) {
        return builderOf(segment.getMid(), segment.getParent()).build();
    }


    @Override
    public String toString() {
        return getType().name() + ":" + getParentMid() + ":" + getChildMid();

    }

    @Override
    public int compareTo(@NonNull RecursiveMemberRef memberRef) {

        if(this.index != null && memberRef.index != null && !this.index.equals(memberRef.index)) {
            return this.index - memberRef.index;
        }

        if(this.getParentMid() != null
            && memberRef.getParentMid() != null) {
            return getParentMid().compareTo(memberRef.getParentMid());
        }

        return this.hashCode() - memberRef.hashCode();
    }
}


