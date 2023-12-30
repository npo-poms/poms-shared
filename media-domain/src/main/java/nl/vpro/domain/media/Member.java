package nl.vpro.domain.media;

import java.io.Serial;
import java.io.Serializable;

import javax.xml.bind.annotation.*;

/**
 * This combines a MemberRef with MediaObject. This represents a member 'from the other side'.
 * Because:
 * - The XML representation of a MemberRef does only make sense as a sub XML of its parent.
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "memberType",
    propOrder = {})
public class Member implements Serializable {

    @Serial
    private static final long serialVersionUID = 8447129713109420423L;

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
