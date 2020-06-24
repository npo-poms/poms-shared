package nl.vpro.domain.media;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 5.13
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parentRefType")
@JsonPropertyOrder({
    "midRef",
    "urnRef",
    "type",
    "memberOf",
    "episodeOf"
})
@Getter
public class ParentRef implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    protected String midRef;

    @XmlAttribute
    protected MediaType type;

    @XmlElement(name = "memberOf")
    private final List<MemberRef> memberOfList = new ArrayList<>();

    @XmlElement(name = "episodeOf")
    private final List<MemberRef> episodeOfList = new ArrayList<>();

    public ParentRef() {
    }

    public ParentRef(@NonNull MediaObject parent) {

        if (parent != null) {
            this.midRef = parent.getMid();
            this.type = parent.getMediaType();
            memberOfList.addAll(parent.getMemberOf());
            if (parent instanceof Program) {
                episodeOfList.addAll(((Program) parent).getEpisodeOf());
            }
        }
    }
}
