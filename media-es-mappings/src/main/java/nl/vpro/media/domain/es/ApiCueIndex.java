package nl.vpro.media.domain.es;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class ApiCueIndex {

    public static String NAME = "subtitles";

    public static String source() {
        return source("setting/cue.json");
    }

    public static String source(String s) {
        return ApiMediaIndex.source(s);
    }
}
