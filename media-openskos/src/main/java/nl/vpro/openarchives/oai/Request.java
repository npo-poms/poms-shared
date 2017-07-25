package nl.vpro.openarchives.oai;

import lombok.Data;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.ZonedDateTimeXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */

@XmlAccessorType(XmlAccessType.NONE)
@Data
public class Request {

    @XmlAttribute
    private String verb;

    @XmlAttribute
    private String metadataPrefix;

    @XmlAttribute
    private String set;

    @XmlAttribute
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime from;

    @XmlAttribute
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime until;


    @XmlValue
    private String value;
}
