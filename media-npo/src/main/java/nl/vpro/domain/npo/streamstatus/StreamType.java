package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.EnumAdapter;

@XmlEnum
@XmlJavaTypeAdapter(StreamType.Adapter.class)
public enum StreamType {

    vod,
    channel,
    live,
    dvr,
    aod;
    public static class Adapter extends EnumAdapter<StreamType> {
        protected Adapter() {
            super(StreamType.class);
        }
    }
}
