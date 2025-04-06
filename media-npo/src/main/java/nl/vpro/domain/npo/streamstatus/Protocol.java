package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.EnumAdapter;

@XmlEnum
@XmlJavaTypeAdapter(Protocol.Adapter.class)
public enum Protocol {
    dash,
    hls,
    smooth,
    progressive

    ;

    public static class Adapter extends EnumAdapter<Protocol> {
        protected Adapter() {
            super(Protocol.class);
        }
    }
}
