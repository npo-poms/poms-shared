package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 5.13
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "recursiveMemberRef",
    propOrder = {
        "memberOf",
        "episodeOf",
        "segmentOf"}
)
@JsonPropertyOrder({
    "midRef",
    "type",
    "memberOf",
    "episodeOf",
    "segmentOf"
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

	@XmlTransient
    private Set<String> stack;

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
        RecursiveMemberRef segmentOf,
        Set<String> stack
    ) {
        this.childMid = childMid;
        this.midRef = parentMid;
        this.type = parentType;
        this.index = index;
        this.highlighted = highlighted == null || ! highlighted ? null : Boolean.TRUE;
        this.memberOf = memberOf;
        this.episodeOf = episodeOf;
        this.segmentOf = segmentOf;
        this.stack = stack;
    }


    private static RecursiveMemberRef.Builder builderOf(String childMid, MediaObject parent, Set<String> memberStack) {
        RecursiveMemberRef.Builder builder =  RecursiveMemberRef.builder()
            .childMid(childMid);

        if (parent != null) {
            builder.parentMid(parent.getMid())
                    .parentType(parent.getMediaType())
                    .memberOf(of(parent.getMemberOf(), memberStack))
                    .episodeOf(parent instanceof Program ? of(((Program) parent).getEpisodeOf(), memberStack) : null)
                    .segmentOf(parent instanceof Segment ? of(((Segment) parent)) : null)
                    .stack(memberStack)
            ;
        }
        return builder;
    }

    private static RecursiveMemberRef of(MemberRef ref, Set<String> stack) {
        RecursiveMemberRef.Builder builder;
        if (ref.getGroup() != null) {
            builder = builderOf(
                ref.getChildMid(),
                ref.getGroup(),
                stack
            );
        } else {
            builder = builder()
                .childMid(ref.getChildMid())
                .parentMid(ref.getParentMid())
                .parentType(ref.getType())
                .stack(stack)
            ;
        }
        return builder
            .index(ref.getNumber())
            .highlighted(ref.isHighlighted())
            .build();
    }

    static SortedSet<RecursiveMemberRef> of(Set<MemberRef> ref, Set<String> stack) {
        if (ref == null) {
            return null;
        }
        TreeSet<RecursiveMemberRef> result = new TreeSet<>();
        ref.forEach((r) -> {
            if (stack.add(r.getMidRef() + ":" + r.getNumber())) {
                RecursiveMemberRef rr = of(r, new TreeSet<>(stack));
                result.add(rr);
            }}
        );
        return result;
    }

    public static RecursiveMemberRef of(Segment segment) {
        return builderOf(
                segment.getMid(),
                segment.getParent(),
                new LinkedHashSet<>()
        ).build();
    }


    @Override
    public String toString() {
        return (getType() == null ? "(unknown type)" : getType().name()) + ":" + getParentMid() + ":" + getChildMid();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecursiveMemberRef that = (RecursiveMemberRef) o;

        if (!midRef.equals(that.midRef)) return false;
        if (!childMid.equals(that.childMid)) return false;
        return index != null ? index.equals(that.index) : that.index == null;
    }

    @Override
    public int hashCode() {
        int result = midRef.hashCode();
        result = 31 * result + childMid.hashCode();
        result = 31 * result + (index != null ? index.hashCode() : 0);
        return result;
    }
}


