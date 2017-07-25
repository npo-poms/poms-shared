package nl.vpro.openarchives.oai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    @XmlElement
    private Header header;
    @XmlElement(name = "metadata")
    private MetaData metaData;
}
