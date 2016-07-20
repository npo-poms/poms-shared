package nl.vpro.media.domain.es;

import java.util.Arrays;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public enum MediaESTypes {
    program,
    group,
    segment,
    deleted,
    memberRef;


    public static String[] toString(MediaESTypes... types) {
        return Arrays.stream(types).map(Enum::name).toArray(String[]::new);

    }

    public static String[] mediaObjects() {
        return toString(program, group, segment);
    }
}
