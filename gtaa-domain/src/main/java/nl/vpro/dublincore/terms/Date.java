package nl.vpro.dublincore.terms;

import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.openarchives.oai.Namespaces;
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

    public Date() {

    }

    public Date(int year, int month, int day) {
        this.value = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.of("UTC"));
    }
}
