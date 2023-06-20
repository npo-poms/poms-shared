package nl.vpro.domain.media;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

/**
 * An representation of a memberRef also having a 'memberRef' attribute in the XML. A {@link MemberRef} doesn't have that
 * because is always represented embedded in a {@link MediaObject}.
 * <p>
 * This object also has {@link #getObjectType()} to make it possible the distinguish {@link Program#getEpisodeOf()} from {@link MediaObject#getMemberOf()}.
 * <p>
 * The original and use case of this object is to be a standalone JSON representation of a 'memberref' relation in poms in ElasticSearch.
 * <p>
 * It is also annotated with {@code javax.persistence} annotations, which makes it possible to easily store this object in other databases too (though poms itself is <em>not</em> doing that).
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
    "objectType"
})
public class StandaloneMemberRef implements Serializable, Ownable, ParentChildRelation {
    private static final long serialVersionUID = 0L;

    protected Instant added;
    protected Boolean highlighted;
    protected MediaType type;
    @Id
    protected Integer index;
    @Id
    protected String midRef;
    @Id
    protected String childRef;
    protected OwnerType owner;
    @Id
    protected ObjectType objectType;

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
        ObjectType objectType) {
        this.added = added;
        this.highlighted = highlighted;
        this.type = type;
        this.index = index;
        this.midRef = midRef;
        this.childRef = childRef;
        this.owner = owner;
        this.objectType = objectType;
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
        public Builder memberRef(MemberRef ref) {
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

    public void setAdded(Instant added) {
        this.added = added;
    }

    @XmlAttribute
    public Boolean getHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }

    @Override
    @XmlAttribute
    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    @XmlAttribute
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @XmlAttribute
    @Override
    public String getMidRef() {
        return midRef;
    }

    public void setMidRef(String midRef) {
        this.midRef = midRef;
    }

    @XmlAttribute
    public String getChildRef() {
        return childRef;
    }

    public void setChildRef(String childRef) {
        this.childRef = childRef;
    }

    @Override
    public String getChildMid() {
        return childRef;
    }

    @XmlAttribute
    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
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
