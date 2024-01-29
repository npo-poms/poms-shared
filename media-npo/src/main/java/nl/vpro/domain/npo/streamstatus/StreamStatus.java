package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlRootElement(name = "streamstatus")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class StreamStatus {
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @NotNull
    Instant timestamp;

    @NotNull
    @XmlElement(required = true)
    String prid;

    String channel;

    @NotNull
    @XmlElement(required = true)
    Encryption encryptie;

    @Valid
    Tijdsbeperking tijdsbeperking;

    @NotNull
    @XmlElement(required = true)
    StreamType streamtype;

    @NotNull
    @XmlElement(required = true)
    Platform platform;

    @NotNull
    @XmlElement(required = true)
    Status status;

    @XmlElementWrapper(name = "profielen")
    List<@Valid Profiel> profiel;

}
