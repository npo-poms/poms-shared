package nl.vpro.domain.npo.streamstatus;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ProfielEncryption {

    playready,
    fairplay,
    cenc
}
