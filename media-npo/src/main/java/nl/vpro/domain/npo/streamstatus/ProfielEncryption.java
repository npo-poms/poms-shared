package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.EnumAdapter;

@XmlEnum
@XmlJavaTypeAdapter(ProfielEncryption.Adapter.class)
public enum ProfielEncryption {

    playready,
    fairplay,
    cenc;

     public static class Adapter extends EnumAdapter<ProfielEncryption> {
        protected Adapter() {
            super(ProfielEncryption.class);
        }
    }
}
