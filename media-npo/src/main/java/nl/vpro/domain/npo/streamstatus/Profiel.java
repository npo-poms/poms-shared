package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

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
