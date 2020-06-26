package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 5.13
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "parentRefType")
@JsonPropertyOrder({
    "midRef",
    "urnRef",
    "type",
    "memberOf",
    "episodeOf"
})
@Getter
@Setter
public class ParentRef implements Serializable, RecursiveParentChildRelation {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    protected String midRef;

    @XmlAttribute
    protected MediaType type;

    @XmlElement(name = "memberOf")
    private List<MemberRef> memberOf;

    @XmlElement(name = "episodeOf")
    private List<MemberRef> episodeOf;

    @XmlTransient
    private String segmentMid;

    public ParentRef() {
    }

    public ParentRef(String segment, @NonNull MediaObject parent) {
        this.segmentMid = segment;
        this.midRef = parent.getMid();
        this.type = parent.getMediaType();
        this.memberOf = new ArrayList<>();
        memberOf.addAll(parent.getMemberOf());
        if (parent instanceof Program) {
            this.episodeOf = new ArrayList<>();
            episodeOf.addAll(((Program) parent).getEpisodeOf());
        }
    }

    public ParentRef(String segment, @NonNull String parentMid, @NonNull MediaType parentType) {
        this.segmentMid = segment;
        this.midRef = parentMid;
        this.type = parentType;
    }

    @Override
    public String getChildMid() {
        return segmentMid;
    }

    @Override
    public ParentRef getSegmentOf() {
        return null;
    }

    @Override
    public String toString() {
        return getParentMid() + "(" + getType() + ")" + ":segment " + getChildMid();

    }
}


