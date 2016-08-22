package nl.vpro.media.domain.es;

import java.util.Arrays;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public enum MediaESType {
    program,
    group,
    segment,
    deletedprogram,
    deletedgroup,
    deletedsegment,
    memberRef;


    private final String source;

    MediaESType(String s) {
        source = s;
    }

    MediaESType() {
        source = name();
    }
    public String source() {
        return ApiMediaIndex.source("mapping/" + source + ".json");
    }

    public static MediaESType[] MEDIAOBJECTS = {program, group, segment};

    public static MediaESType[] DELETED_MEDIAOBJECTS = {deletedprogram, deletedgroup, deletedsegment};


    public static String[] toString(MediaESType... types) {
        return Arrays.stream(types).map(Enum::name).toArray(String[]::new);

    }

    public static String[] mediaObjects() {
        return toString(MEDIAOBJECTS);
    }

    public static String[] deletedMediaObjects() {
        return toString(DELETED_MEDIAOBJECTS);
    }
}
