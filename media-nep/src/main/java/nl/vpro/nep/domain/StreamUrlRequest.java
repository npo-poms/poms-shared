package nl.vpro.nep.domain;

import java.time.Duration;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@lombok.Data
public class StreamUrlRequest  {

    private Data data;

    public StreamUrlRequest(String ip, Duration duration) {
        data = new Data();
        data.attributes = new Attributes();
        data.attributes.ip = ip;
        data.attributes.duration = duration;
    }

    @lombok.Data
    static class Attributes {
        String viewer = "pomsgui";
        String ip;
        Duration duration;
    }

    @lombok.Data
    static class Data {
        String type = "access";
        Attributes attributes;

    }
}
