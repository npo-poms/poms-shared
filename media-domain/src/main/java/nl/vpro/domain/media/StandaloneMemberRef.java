package nl.vpro.domain.media;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * An representation of a memberRef also having a 'memberRef' attribute.
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class StandaloneMemberRef {

    protected Instant added;
    protected Boolean highlighted;
    protected MediaType type;
    protected Integer index;
    protected String midRef;
    protected String childRef;
    protected ObjectType objectType;


    public StandaloneMemberRef(String childRef, MemberRef ref, ObjectType objectType) {
        this.childRef = childRef;
        added = ref.getAdded();
        highlighted = ref.isHighlighted();
        type = ref.getType();
        midRef = ref.getMidRef();
        index = ref.getNumber();
        this.objectType = objectType;

    }

    public static class Builder {
        public Builder memberRef(MemberRef ref) {
            return
                added(ref.getAdded())
                    .highlighted(ref.isHighlighted())
                    .type(ref.getType())
                    .midRef(ref.getMidRef())
                    .index(ref.getNumber())
                ;
        }
    }

    @XmlTransient
    public String getId() {
        return midRef+ "/" + (index == null ? "_" : index) + "/" + childRef + "/" + objectType;
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

    @XmlAttribute
    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public enum ObjectType {
        memberRef,
        episodeRef
    }
}
