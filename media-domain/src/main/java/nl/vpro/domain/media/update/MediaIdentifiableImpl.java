package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.MediaIdentifiable;
import nl.vpro.validation.StringList;

/**
* @author Michiel Meeuwissen
* @since 3.4
*/
@XmlRootElement(name = "midAndType")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "midAndTypeType")
public class MediaIdentifiableImpl implements Serializable, MediaIdentifiable {

    @Serial
    private static final long serialVersionUID = 0L;

    @XmlAttribute
    @Getter
    private String mid;


    @XmlAttribute
    @Getter
    private Long  id;

    @XmlAttribute(name = "type")
    @Getter
    private MediaType mediaType;

    @XmlElement(name = "crid")
    @StringList(pattern = "(?i)crid://.*/.*")
    @Getter
    @Setter
    private List<String> crids;

    public MediaIdentifiableImpl() {
        // required by JAXB.
    }

    public MediaIdentifiableImpl(String mid, MediaType type, List<String> crids) {
        this.mid = mid;
        this.mediaType = type;
        this.crids = crids == null ? Collections.emptyList() : Collections.unmodifiableList(crids);
    }



    @Override
    public String toString() {
        return mediaType + " " + mid + crids;
    }

}
