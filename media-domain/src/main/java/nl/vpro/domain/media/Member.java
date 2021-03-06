package nl.vpro.domain.media;

import javax.xml.bind.annotation.*;

/**
 * This combines a MemberRef with MediaObject. This represents a member 'form the other side'.
 * Because:
 * - The XML representation of a MemberRef does only make sense as a sub XML of its parent.
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberType",
    propOrder = {})
public class Member {

    @XmlElement(required = true)
    private MediaObject member;

    @XmlAttribute
    private boolean highlighted;
    @XmlAttribute
    private Integer number;

    private Member() {
        // just to satisfy jaxb
    }

    public Member(MemberRef ref) {
        member = ref.getMember();
        highlighted = ref.isHighlighted();
        number = ref.getNumber();
    }

    public boolean isHighlighted() {
        return highlighted;
    }
    public Integer getNumber() {
        return number;
    }
    public MediaObject getMember() {
        return member;
    }
}
