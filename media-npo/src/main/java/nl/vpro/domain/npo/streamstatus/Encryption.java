package nl.vpro.domain.npo.streamstatus;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Encryption {

    @XmlEnumValue(value = "")
    NONE,
    DRM;

}
