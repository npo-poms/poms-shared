package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum ProfielEncryption {

    playready,
    fairplay,
    cenc
}
