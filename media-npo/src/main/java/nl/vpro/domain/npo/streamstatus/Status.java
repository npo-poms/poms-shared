package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Status {
    online,
    /**
     * TODO: no proof seen that this occurs
     */
    offline
}
