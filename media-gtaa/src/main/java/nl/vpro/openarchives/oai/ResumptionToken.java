package nl.vpro.openarchives.oai;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

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
