package nl.vpro.media.domain.es;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class ApiCueIndex {

    public static String NAME = ApiMediaIndex.NAME;

    public static String TYPE = "cue";


    public static String typeSource() {
        return source("mapping/cue.json");
    }

    public static String settingSource() {
        return source("setting/apimedia.json");
    }

    public static String source(String s) {
        return ApiMediaIndex.source(s);
    }


}
