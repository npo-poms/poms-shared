package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import java.time.Instant;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
    String prid;

    String channel;

    @NotNull
    Encryption encryptie;

    @Valid
    Tijdsbeperking tijdsbeperking;

    @NotNull
    StreamType streamtype;

    @NotNull
    Platform platform;

    @NotNull
    Status status;

    @XmlElementWrapper(name = "profielen")
    List<@Valid Profiel> profiel;

}
