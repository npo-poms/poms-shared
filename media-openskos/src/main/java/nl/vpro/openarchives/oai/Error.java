package nl.vpro.openarchives.oai;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
public class Error {
    @XmlAttribute(namespace = "")
    private String code;

    @XmlValue
    private String message;

    public Error() {

    }
}
