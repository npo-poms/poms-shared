package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlRootElement(name = "streamstatus")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class StreamStatus {
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    Instant timestamp;
    String prid;
    String channel;
    Encryption encryptie;
    Tijdsbeperking tijdsbeperking;
    String streamtype;
    String platform;
    String status;
    @XmlElementWrapper(name = "profielen")
    List<Profiel> profiel;

}
