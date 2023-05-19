package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Profiel {
    int versie;

    @NotNull
    Protocol protocol;

    ProfielEncryption encryptie;
}
