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



    public String source() {
        return ApiMediaIndex.source("mapping/" + name() + ".json");
    }

    public static MediaESType[] MEDIAOBJECTS = {program, group, segment};


    public static String[] toString(MediaESType... types) {
        return Arrays.stream(types).map(Enum::name).toArray(String[]::new);

    }

    public static String[] mediaObjects() {
        return toString(MEDIAOBJECTS);
    }
}
