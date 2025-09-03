package nl.vpro.domain.npo.mediastatus;

import jakarta.xml.bind.annotation.*;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class StreamCount {

    @XmlElement
    private int video;

    @XmlElement
    private int audio;

}
