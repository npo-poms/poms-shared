package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Profiel {
    int versie;
    String protocol;
    String encryptie;
}
