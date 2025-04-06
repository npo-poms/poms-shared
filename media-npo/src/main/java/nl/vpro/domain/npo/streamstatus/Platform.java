package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.EnumAdapter;

@XmlEnum
@XmlJavaTypeAdapter(Platform.Adapter.class)
public enum Platform {
    extra,
    internetvod;

    public static class Adapter extends EnumAdapter<Platform> {
        protected Adapter() {
            super(Platform.class);
        }
    }
}
