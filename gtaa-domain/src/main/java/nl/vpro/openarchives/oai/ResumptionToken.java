package nl.vpro.openarchives.oai;

import lombok.Data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class ResumptionToken {

    @XmlAttribute(namespace = "")
    private Long  cursor;

    @XmlAttribute(namespace = "")
    private Long completeListSize;

    @XmlValue
    private String value;
}
