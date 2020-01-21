package nl.vpro.domain.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * @since 5.12
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
public class ParentRef implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    protected String midRef;

    @XmlAttribute
    protected MediaType type;

    @XmlElement(name = "memberOf")
    private List<MemberRef> memberOfList;

    @XmlElement(name = "episodeOf")
    private List<MemberRef> episodeOfList;

    public ParentRef() {
    }

    public ParentRef(MediaObject parent) {

        if (parent != null) {
            this.midRef = parent.getMid();
            this.type = parent.getMediaType();

            this.memberOfList = new ArrayList<>();
            memberOfList.addAll(parent.getMemberOf());

            this.episodeOfList = new ArrayList<>();
            if (parent instanceof Program) {
                episodeOfList.addAll(((Program) parent).getEpisodeOf());
            }
        }
    }
}
