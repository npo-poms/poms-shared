package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;

import static nl.vpro.domain.media.support.PublishableObject.SERIALIZING;

/**
 * Represents a 'memberref' but in json/xml binding only, where it is used to show all descendants
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

    @Serial
    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Getter
    protected String midRef;

    protected String childMid;

    @JsonBackReference
    ParentChildRelation parent;

    @XmlAttribute
    @Getter
    protected MediaType type;


    @XmlElement(name = "memberOf")
    @Setter
    @Getter
    @JsonManagedReference
    protected SortedSet<RecursiveMemberRef> memberOf;

    @XmlElement(name = "episodeOf")
    @Setter
    @Getter
    @JsonManagedReference
    protected SortedSet<RecursiveMemberRef> episodeOf;

    @Getter
    @XmlElement(name = "segmentOf")
    @JsonManagedReference
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


    /**
     * @param parentType the type of the parent, if known. This is not always known, for instance when this MemberRef is fetched from an {@link nl.vpro.domain.media.update.MemberRefUpdate}.
     */
    @lombok.Builder(builderClassName = "Builder")
    private RecursiveMemberRef(
        @Nullable String childMid,
        @NonNull String parentMid,
        @Nullable MediaType parentType,
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
        this.highlighted = highlighted == null || !highlighted ? null : Boolean.TRUE;
        this.memberOf = memberOf;
        this.episodeOf = episodeOf;
        this.segmentOf = segmentOf;
        this.circular = circular;
    }


    private static RecursiveMemberRef.Builder builderOf(
        @Nullable String childMid, MediaObject parent, Set<StackElement> memberStack) {
        RecursiveMemberRef.Builder builder = RecursiveMemberRef.builder()
            .childMid(childMid);

        if (parent != null) {
            builder.parentMid(parent.getMid())
                .parentType(parent.getMediaType())
                .memberOf(of(parent.getMemberOf(), memberStack, MemberRefType.memberOf))
                .episodeOf(parent instanceof Program parentProgram ? of(parentProgram.getEpisodeOf(), memberStack, MemberRefType.episodeOf) : null)
                .segmentOf(
                    parent instanceof Segment parentSegment ?
                        ofSegment(parentSegment, memberStack) :
                        null
                )
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
        SortedSet<RecursiveMemberRef> result = new TreeSet<>();
        ref.forEach((r) -> {
            final Set<StackElement> copyOfStack = new LinkedHashSet<>(stack);
            final StackElement newStackElement =
                new StackElement(
                    copyOfStack.isEmpty() ? r.getChildMid() : null,
                    r.getMidRef(),
                    mode,
                    r.getNumber()
                );
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
                    .circular(true)
                    .build());
                log.warn("Circular reference detected {}({})", stack.stream().map(StackElement::toString).collect(Collectors.joining("")), newStackElement);
            }
        });
        return result;
    }

    public static RecursiveMemberRef ofSegment(Segment segment) {
        return ofSegment(segment, new LinkedHashSet<>());
    }

    protected static RecursiveMemberRef ofSegment(Segment segment, Set<StackElement> stack) {
        StackElement newStackElement = new StackElement(stack.isEmpty() ? segment.getMid() : null, segment.getParent().getMid(), MemberRefType.segmentOf, null);
        if (stack.add(newStackElement)) {
            return builderOf(
                segment.getMid(),
                segment.getParent(),
                stack
            ).build();
        } else {
            log.warn("Circular reference detected {}({})", stack.stream().map(StackElement::toString).collect(Collectors.joining("")), newStackElement);
            return RecursiveMemberRef.builder()
                .childMid(segment.getMid())
                .parentType(segment.getParent().getMediaType())
                .parentMid(segment.getParent().getMid())
                .circular(true)
                .build();
        }
    }

    /**
     * For certain memberRef, create a set of recursive Members representing the 'memberOf' of the parent of this memberRef
     */
    public static SortedSet<RecursiveMemberRef> memberOfs(MemberRef ref) {
        MediaObject group = ref.getGroup();
        if (group != null) {
            SortedSet<MemberRef> memberOf = group.getMemberOf();

            Set<StackElement> stack = new LinkedHashSet<>();
            if (!SERIALIZING.get()) {
                stack.add(new StackElement(ref.getChildMid(), ref.getParentMid(), ref.getRefType(), ref.getNumber()));
            }
            return of(memberOf, stack, MemberRefType.memberOf);
        } else {
            return Collections.emptySortedSet();
        }
    }


    /**
     * For certain memberRef, create a set of recursive Members representing the 'episode' of the parent of this memberRef
     */
    public static SortedSet<RecursiveMemberRef> episodeOfs(MemberRef ref) {
        MediaObject group = ref.getGroup();
        if (group instanceof Program program) {
            SortedSet<MemberRef> episodeOf = program.getEpisodeOf();
            Set<StackElement> stack = new LinkedHashSet<>();
            if (!SERIALIZING.get()) {
                stack.add(new StackElement(ref.getChildMid(), ref.getParentMid(), ref.getRefType(), ref.getNumber()));
            }
            return of(episodeOf, stack, MemberRefType.episodeOf);
        } else {
            return Collections.emptySortedSet();
        }
    }


    /**
     * If this recursive memberref is marked 'circular' then we have detected that in the current stack the parent is already available.
     * <em>this</em> recursive memberref will <em>not</em> includes <em>its</em> parent, because that would lead to infinite recursion.
     */
    public boolean isCircular() {
        return circular != null && circular;
    }


    @SuppressWarnings("unused")
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof ParentChildRelation) {
            this.parent = (ParentChildRelation) parent;
        }
    }

    @Override
    public String getChildMid() {
        if (this.parent != null) {
            this.childMid = this.parent.getParentMid();
            this.parent = null;
        }
        return this.childMid;
    }


    @Override
    public String toString() {
        return toString(getChildMid());
    }

    public String toString(String childMid) {
        return (getType() == null ? "(unknown type)" : getType().name()) + ":" + getParentMid() + ":" + childMid;
    }

    @Override
    public int compareTo(@NonNull RecursiveMemberRef memberRef) {

        if (this.index != null && memberRef.index != null && !this.index.equals(memberRef.index)) {
            return this.index - memberRef.index;
        }

        if (this.getParentMid() != null
            && memberRef.getParentMid() != null) {
            return getParentMid().compareTo(memberRef.getParentMid());
        }

        return this.hashCode() - memberRef.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecursiveMemberRef that)) {
            return false;
        }

        if (!midRef.equals(that.midRef)) return false;
        if (!getChildMid().equals(that.getChildMid())) return false;
        return index != null ? index.equals(that.index) : that.index == null;
    }

    @Override
    public int hashCode() {
        getChildMid();
        int result = midRef == null ? 0 : midRef.hashCode();
        result = 31 * result + (childMid == null ? 0 : childMid.hashCode());
        result = 31 * result + (index != null ? index.hashCode() : 0);
        return result;
    }

    protected static class StackElement {
        private final String child;
        private final String parent;
        private final MemberRefType type;
        private final Integer number;


        private StackElement(
            @NonNull String child,
            @NonNull String parent,
            @NonNull MemberRefType type,
            @Nullable Integer number) {
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

            return Objects.equals(parent, that.parent);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(parent);
        }

        @Override
        public String toString() {
            return (child == null ? "" : child) + " -" + type + (number != null ? (":" + number) : "") + "-> " + parent;
        }
    }

    public static class Builder {

    }

}


