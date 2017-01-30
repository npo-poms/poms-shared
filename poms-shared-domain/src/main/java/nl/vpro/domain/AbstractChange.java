package nl.vpro.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@XmlTransient
@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractChange<T>  {


    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant publishDate;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private Boolean deleted;

    @XmlAttribute
    private Boolean tail = null;


    @XmlElement(name = "object")
    private T object;

    protected AbstractChange() {

    }


}
