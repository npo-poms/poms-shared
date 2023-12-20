package nl.vpro.domain.media;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

/**
 * A representation of a memberRef also having a 'memberRef' attribute in the XML. A {@link MemberRef} doesn't have that
 * because is always represented embedded in a {@link MediaObject}.
 * <p>
 * This object also has {@link #getObjectType()} to make it possible to distinguish {@link Program#getEpisodeOf()} from {@link MediaObject#getMemberOf()}.
 * <p>
 * The original and use case of this object is to be a standalone JSON representation of a 'memberref' relation in poms in ElasticSearch.
 * <p>
 * It is also annotated with {@code javax.persistence} annotations, which makes it possible to easily store this object in other databases too (though poms itself is <em>not</em> doing that).
 * <p>
 * There a several use cases for this class which <em>are</em> implemented:
 * <ul>
 *     <li>It is the json representation of a memberref of episoderef in separate elasticsearch</li>
 *     <li>It serves as a standalone update object for both member and episode ref, in the broadcaster importers (by MSE-5199)</li>
 * </ul>
 * <p>
 *
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlType(name = "standaloneMemberRefType")
@JsonPropertyOrder({
    "midRef",
    "childRef",
    "type",
    "index",
    "highlighted",
    "objectType",
    "delete"
})
@IdClass(StandaloneMemberRef.IdType.class)
public class StandaloneMemberRef implements Serializable, Ownable, ParentChildRelation {
    @Serial
    private static final long serialVersionUID = 0L;

    @Setter
    protected Instant added;
    @Setter
    protected Boolean highlighted;
    @Setter
    protected MediaType type;
    @Setter
    @Id
    protected Integer index;
    @Setter
    @Id
    protected String midRef;
    @Setter
    @Id
    protected String childRef;
    protected OwnerType owner;
    @Setter
    @Id
    protected ObjectType objectType;

    @Getter
    @XmlAttribute
    private Boolean delete;

    public StandaloneMemberRef() {

    }

    @lombok.Builder(builderClassName = "Builder")
    private StandaloneMemberRef(
        Instant added,
        Boolean highlighted,
        MediaType type,
        Integer index,
        String midRef,
        String childRef,
        OwnerType owner,
        ObjectType objectType,
        Boolean delete) {
        this.added = added;
        this.highlighted = highlighted;
        this.type = type;
        this.index = index;
        this.midRef = midRef;
        this.childRef = childRef;
        this.owner = owner;
        this.objectType = objectType;
        this.delete = (delete == null || ! delete) ? null : delete;
    }

    public StandaloneMemberRef(String childRef, MemberRef ref, ObjectType objectType) {
        this.childRef = childRef;
        added = ref.getAdded();
        highlighted = ref.isHighlighted();
        type = ref.getType();
        midRef = ref.getMidRef();
        index = ref.getNumber();
        this.objectType = objectType;
        this.owner = ref.getOwner();
    }
    public StandaloneMemberRef(StandaloneMemberRef ref) {
        childRef = ref.childRef;
        added = ref.added;
        highlighted = ref.highlighted;
        type = ref.type;
        midRef = ref.midRef;
        index = ref.index;
        objectType = ref.objectType;
        owner = ref.owner;
    }

    public static StandaloneMemberRef memberRef(String child, MemberRef ref) {
        return new StandaloneMemberRef(child, ref, ObjectType.memberRef);
    }
    public static StandaloneMemberRef episodeRef(String child, MemberRef ref) {
        return new StandaloneMemberRef(child, ref, ObjectType.episodeRef);
    }

    @Override
    @XmlAttribute
    public @NonNull OwnerType getOwner() {
        return owner;
    }


    public static class Builder {

        public Builder episodeRef(MemberRef ref) {
            return _memberRef(ref)
                .episodeRef();
        }

        public Builder memberRef(MemberRef ref) {
            return _memberRef(ref)
                .memberRef();
        }

        public Builder episodeRef() {
            return objectType(ObjectType.episodeRef);
        }

        public Builder memberRef() {
            return objectType(ObjectType.memberRef);
        }

        public Builder _memberRef(MemberRef ref) {
            return
                added(ref.getAdded())
                    .highlighted(ref.isHighlighted())
                    .type(ref.getType())
                    .midRef(ref.getMidRef())
                    .index(ref.getNumber())
                    .owner(ref.getOwner())
                ;
        }
    }

    public MemberRef toMemberRef() {
        return MemberRef
            .builder()
            .added(added)
            .type(type)
            .midRef(midRef)
            .number(index)
            .highlighted(highlighted)
            .owner(owner)
            .build();
    }

    @XmlTransient
    public IdType getId() {
        return new IdType(
                midRef,
                index,
                childRef,
                objectType);
    }

    @XmlAttribute
    public Instant getAdded() {
        return added;
    }

    @XmlAttribute
    public Boolean getHighlighted() {
        return highlighted;
    }

    @Override
    @XmlAttribute
    public MediaType getType() {
        return type;
    }

    @XmlAttribute
    public Integer getIndex() {
        return index;
    }

    @XmlAttribute
    @Override
    public String getMidRef() {
        return midRef;
    }

    @XmlAttribute
    public String getChildRef() {
        return childRef;
    }

    @Override
    public String getChildMid() {
        return childRef;
    }

    @XmlAttribute
    public ObjectType getObjectType() {
        return objectType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StandaloneMemberRef that = (StandaloneMemberRef) o;

        if (added != null ? !added.equals(that.added) : that.added != null) return false;
        if (highlighted != null ? !highlighted.equals(that.highlighted) : that.highlighted != null) return false;
        if (type != that.type) return false;
        if (index != null ? !index.equals(that.index) : that.index != null) return false;
        if (midRef != null ? !midRef.equals(that.midRef) : that.midRef != null) return false;
        if (childRef != null ? !childRef.equals(that.childRef) : that.childRef != null) return false;
        if (owner != that.owner) return false;
        return objectType == that.objectType;
    }

    @Override
    public int hashCode() {
        int result = added != null ? added.hashCode() : 0;
        result = 31 * result + (highlighted != null ? highlighted.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (index != null ? index.hashCode() : 0);
        result = 31 * result + (midRef != null ? midRef.hashCode() : 0);
        result = 31 * result + (childRef != null ? childRef.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (objectType != null ? objectType.hashCode() : 0);
        return result;
    }

    public enum ObjectType {
        memberRef,
        episodeRef
    }

    @Override
    public String toString () {
        return (type == null ? "" : (type + ":")) + getId() + "(" + added + ")";
    }

    @NoArgsConstructor
    @EqualsAndHashCode
    public static class IdType implements Serializable{
        @Serial
        private static final long serialVersionUID = 8764152600088414011L;

        String midRef;
         String index;
         String childRef;
         ObjectType objectType;

        public IdType(String midRef, Integer index, String childRef, ObjectType objectType) {
            this.midRef = midRef;
            this.index = (index == null ? "_" : String.valueOf(index));
            this.childRef = childRef;
            this.objectType = objectType;
        }

        @Override
         public String toString() {
             return midRef + "/" + index + "/" + childRef + (objectType == null ? "" : ("/" + objectType));
         }

    }
}
