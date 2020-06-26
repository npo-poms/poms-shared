package nl.vpro.domain.media;

import lombok.Getter;

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
public class ParentRef implements Serializable, RecursiveParentChildRelation {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    protected String midRef;

    @XmlAttribute
    protected MediaType type;

    @XmlElement(name = "memberOf")
    private final List<MemberRef> memberOf = new ArrayList<>();

    @XmlElement(name = "episodeOf")
    private final List<MemberRef> episodeOf = new ArrayList<>();

    @XmlTransient
    private String segmentMid;

    public ParentRef() {
    }

    public ParentRef(String segment, @NonNull MediaObject parent) {
        this.segmentMid = segment;
        if (parent != null) {
            this.midRef = parent.getMid();
            this.type = parent.getMediaType();
            memberOf.addAll(parent.getMemberOf());
            if (parent instanceof Program) {
                episodeOf.addAll(((Program) parent).getEpisodeOf());
            }
        }
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


