package nl.vpro.domain.npo.streamstatus;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum Status {
    online,
    /**
     * TODO: no proof seen that this occurs
     */
    offline
}
