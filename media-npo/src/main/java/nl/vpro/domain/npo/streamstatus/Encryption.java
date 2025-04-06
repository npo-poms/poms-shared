package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.EnumAdapter;

@XmlEnum
@XmlJavaTypeAdapter(Encryption.Adapter.class)
public enum Encryption {

    @XmlEnumValue(value = "")
    NONE,
    DRM;

    public static class Adapter extends EnumAdapter<Encryption> {
        protected Adapter() {
            super(Encryption.class);
        }
    }

}
