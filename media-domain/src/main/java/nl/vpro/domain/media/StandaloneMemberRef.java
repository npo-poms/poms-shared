package nl.vpro.domain.media;

import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

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
    "highlighted"
})
public class StandaloneMemberRef {

    protected Instant added;
    protected Boolean highlighted;
    protected MediaType type;
    protected Integer index;
    protected String midRef;
    protected String childRef;

    public StandaloneMemberRef(String childRef, MemberRef ref) {
        this.childRef = childRef;

        added = ref.getAdded();
        highlighted = ref.isHighlighted();
        type = ref.getType();
        midRef = ref.getMidRef();
        index = ref.getNumber();

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
}
