package nl.vpro.openarchives.oai;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
public class Error {
    /**
     * Code associated with this error
     */
    @XmlAttribute(namespace = "")
    private String code;

    @XmlValue
    private String message;

    public Error() {

    }
}
