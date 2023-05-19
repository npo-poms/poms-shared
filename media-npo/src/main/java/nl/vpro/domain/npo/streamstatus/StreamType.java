package nl.vpro.domain.npo.streamstatus;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum StreamType {

    vod,
    channel,
    live,
    dvr,
    aod
}
