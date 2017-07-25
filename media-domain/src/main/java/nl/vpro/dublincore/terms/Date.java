package nl.vpro.dublincore.terms;

import lombok.Data;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.media.gtaa.Namespaces;
import nl.vpro.xml.bind.ZonedDateTimeXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public class Date {

    @XmlAttribute(namespace = Namespaces.RDF)
    private String datatype;

    @XmlValue
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime value;
}
