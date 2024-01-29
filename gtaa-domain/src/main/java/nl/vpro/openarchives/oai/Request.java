package nl.vpro.openarchives.oai;

import lombok.Data;

import java.time.ZonedDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
