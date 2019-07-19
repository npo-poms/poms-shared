package nl.vpro.openarchives.oai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.ZonedDateTimeXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {

    public Header(String identifier, ZonedDateTime dt) {
        this(null, identifier, dt, null);
    }
    @XmlAttribute
    String status;
    @XmlElement
    String identifier;
    @XmlElement
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    ZonedDateTime datestamp;
    @XmlElement
    List<String> setSpec;

}
