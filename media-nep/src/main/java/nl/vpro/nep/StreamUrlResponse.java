package nl.vpro.nep;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@lombok.Data
public class StreamUrlResponse {

    Data data;

    @lombok.Data
    public static class Data {

        Attributes attributes;

    }
    @lombok.Data
    public static class Attributes {
        String url;
        String type;
        String cdn;
        String cdnType;
        boolean drm;
        boolean legacy;
    }
}
