package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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

	@XmlAttribute
    Boolean circular;

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
        Boolean circular
    ) {
        this.childMid = childMid;
        this.midRef = parentMid;
        this.type = parentType;
        this.index = index;
        this.highlighted = highlighted == null || ! highlighted ? null : Boolean.TRUE;
        this.memberOf = memberOf;
        this.episodeOf = episodeOf;
        this.segmentOf = segmentOf;
        this.circular = circular;
    }


    private static RecursiveMemberRef.Builder builderOf(String childMid, MediaObject parent, Set<StackElement> memberStack) {
        RecursiveMemberRef.Builder builder =  RecursiveMemberRef.builder()
            .childMid(childMid);

        if (parent != null) {
            builder.parentMid(parent.getMid())
                    .parentType(parent.getMediaType())
                    .memberOf(of(parent.getMemberOf(), memberStack,  MemberRefType.memberOf))
                    .episodeOf(parent instanceof Program ? of(((Program) parent).getEpisodeOf(), memberStack, MemberRefType.episodeOf) : null)
                    .segmentOf(parent instanceof Segment ? of(((Segment) parent)) : null)
            ;
        }
        return builder;
    }

    private static RecursiveMemberRef of(MemberRef ref, Set<StackElement> stack) {
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
            ;
        }
        return builder
            .index(ref.getNumber())
            .highlighted(ref.isHighlighted())
            .build();
    }

    private static SortedSet<RecursiveMemberRef> of(Set<MemberRef> ref, Set<StackElement> stack, MemberRefType mode) {
        if (ref == null) {
            return null;
        }
        TreeSet<RecursiveMemberRef> result = new TreeSet<>();
        ref.forEach((r) -> {
            LinkedHashSet<StackElement> copyOfStack = new LinkedHashSet<>(stack);
            StackElement newStackElement = new StackElement(copyOfStack.isEmpty() ? r.getChildMid() : null, r.getMidRef(), mode, r.getNumber());
            if (copyOfStack.add(newStackElement)) {
                RecursiveMemberRef rr = of(r, copyOfStack);
                result.add(rr);
            } else {
                // break recursion
                result.add(RecursiveMemberRef.builder()
                    .childMid(r.getChildMid())
                    .parentType(r.getType())
                    .parentMid(r.getParentMid())
                    .index(r.getNumber())
                    .circular(true).build());
                log.warn("Circular reference detected {}({})", stack.stream().map(StackElement::toString).collect(Collectors.joining("")), newStackElement);
            }
        });
        return result;
    }

    public static RecursiveMemberRef of(Segment segment) {
        return builderOf(
                segment.getMid(),
                segment.getParent(),
                new LinkedHashSet<>()
        ).build();
    }

    static SortedSet<RecursiveMemberRef> memberOfs(MemberRef ref, SortedSet<MemberRef> memberOf) {
        Set<StackElement> stack = new LinkedHashSet<>();
        stack.add(new StackElement(ref.getChildMid(), ref.getParentMid(), ref.getRefType(), ref.getNumber()));
        return of(memberOf, stack, MemberRefType.memberOf);
    }

    static SortedSet<RecursiveMemberRef> episodeOfs(MemberRef ref, SortedSet<MemberRef> memberOf) {
        Set<StackElement> stack = new LinkedHashSet<>();
        stack.add(new StackElement(ref.getChildMid(), ref.getParentMid(), ref.getRefType(), ref.getNumber()));
        return of(memberOf, stack, MemberRefType.episodeOf);
    }


    /**
     * If this recursive memberref is marked 'circular' then we have detected that in the current stack the parent is already available.
     * <em>this</em> recursive memberref will <em>not</em> includes <em>its</em> parent, because that would lead to infinite recursion.
     */
    public boolean isCircular() {
        return circular != null && circular;
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
        int result = midRef == null ? 0 : midRef.hashCode();
        result = 31 * result + (childMid == null ? 0 : childMid.hashCode());
        result = 31 * result + (index != null ? index.hashCode() : 0);
        return result;
    }

    private static class StackElement {
        private final String child;
        private final String parent;
        private final MemberRefType type;
        private final Integer number;


        private StackElement(String child, String parent, MemberRefType type, Integer number) {
            this.parent = parent;
            this.child = child;
            this.type = type;
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StackElement that = (StackElement) o;

            return parent.equals(that.parent);
        }

        @Override
        public int hashCode() {
            return parent.hashCode();
        }

        @Override
        public String toString() {
            return (child == null ? "" : child) + " -" + type + ":" + number + "-> " + parent;
        }
    }

}


