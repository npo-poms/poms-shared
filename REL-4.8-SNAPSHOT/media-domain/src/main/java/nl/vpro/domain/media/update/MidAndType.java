package nl.vpro.domain.media.update;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.MediaType;
import nl.vpro.validation.StringList;

/**
* @author Michiel Meeuwissen
* @since 3.4
*/
@XmlRootElement(name = "midAndType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "midAndTypeType")
public class MidAndType implements Serializable {

    private static final long serialVersionUID = 0l;

    @XmlAttribute
    private String mid;

    @XmlAttribute
    private MediaType type;

    @XmlElement(name = "crid")
    @StringList(pattern = "(?i)crid://.*/.*")
    private List<String> crids;

    public MidAndType() {
        // required by JAXB.
    }

    public MidAndType(String mid, MediaType type, List<String> crids) {
        this.mid = mid;
        this.type = type;
        this.crids = Collections.unmodifiableList(crids);
    }

    public String getMid() {
        return mid;
    }

    public MediaType getType() {
        return type;
    }

    public List<String> getCrids() {
        return crids;
    }

    public void setCrids(List<String> crids) {
        this.crids = crids;
    }

    public String getCorrelationId() {
        if (mid != null) {
            return mid;
        } else if (crids != null && ! crids.isEmpty()) {
            return crids.get(0);
        } else {
            return "" + hashCode();
        }
    }

    @Override
    public String toString() {
        return type + " " + mid + crids;
    }

}
