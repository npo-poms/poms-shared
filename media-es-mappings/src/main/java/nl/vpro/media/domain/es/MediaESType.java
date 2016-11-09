package nl.vpro.media.domain.es;

import java.util.Arrays;
import java.util.stream.Stream;

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
    programMemberRef,
    groupMemberRef,
    segmentMemberRef,
    episodeRef
    ;



    public String source() {
        return ApiMediaIndex.source("mapping/" + name() + ".json");
    }

    public static MediaESType[] MEDIAOBJECTS = {program, group, segment};

    public static MediaESType[] MEMBERREFS= {programMemberRef, groupMemberRef, segmentMemberRef};

    public static MediaESType[] DELETED_MEDIAOBJECTS = {deletedprogram, deletedgroup, deletedsegment};


    public static String[] toString(MediaESType... types) {
        return Arrays.stream(types).map(Enum::name).toArray(String[]::new);

    }

    public static String[] mediaObjects() {
        return toString(MEDIAOBJECTS);
    }


    public static String[] memberRefs() {
        return toString(MEMBERREFS);
    }

    public static String[] deletedMediaObjects() {
        return toString(DELETED_MEDIAOBJECTS);
    }

    public static String[] nonRefs() {
        return toString(Stream.concat(Arrays.stream(MEDIAOBJECTS), Arrays.stream(DELETED_MEDIAOBJECTS)).toArray(MediaESType[]::new));
    }
}
