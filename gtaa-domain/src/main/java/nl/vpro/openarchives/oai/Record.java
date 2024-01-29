package nl.vpro.openarchives.oai;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public boolean isDeleted() {
        return getMetaData() == null;
    }
}
