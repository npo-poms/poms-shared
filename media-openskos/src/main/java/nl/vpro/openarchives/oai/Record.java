package nl.vpro.openarchives.oai;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class Record {

    @XmlElement
    private Header header;
    @XmlElement(name = "metadata")
    private MetaData metaData;
}
