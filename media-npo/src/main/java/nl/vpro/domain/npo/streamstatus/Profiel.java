package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Profiel {
    @XmlElement(required = true)
    int versie;

    @NotNull
    @XmlElement(required = true)
    Protocol protocol;

    ProfielEncryption encryptie;
}
