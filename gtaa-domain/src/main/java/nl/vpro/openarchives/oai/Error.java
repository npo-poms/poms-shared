package nl.vpro.openarchives.oai;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
@AllArgsConstructor
@lombok.Builder
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
