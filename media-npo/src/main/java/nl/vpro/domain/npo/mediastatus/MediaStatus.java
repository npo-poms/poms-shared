package nl.vpro.domain.npo.mediastatus;

import lombok.Data;

import java.time.Instant;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlRootElement(name = "mediastatus")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class MediaStatus {

    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    private Instant timestamp;

    @XmlElement
    @XmlJavaTypeAdapter(TrimAdapter.class)
    private String duration;

    @XmlElement
    private int framerate;

    @XmlElement
    private int width;

    @XmlElement
    private int height;


    @XmlElement
    @XmlJavaTypeAdapter(TrimAdapter.class)
    private String mid;

    @XmlElement
    private StreamCount streamCount;

}
