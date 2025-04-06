package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.EnumAdapter;

@XmlEnum
@XmlJavaTypeAdapter(Status.Adapter.class)
public enum Status {
    online,
    /**
     * TODO: no proof seen that this occurs
     */
    offline;

    public static class Adapter extends EnumAdapter<Status> {
        protected Adapter() {
            super(Status.class);
        }
    }
}
