package nl.vpro.pages.domain.es;

import java.util.Arrays;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public enum PagesESType {
    page,
    deletedpage("page")
    ;

    private final String source;

    PagesESType(String s) {
        source = s;
    }

    PagesESType() {
        source = name();
    }
    public String source() {
        return ApiPagesIndex.source("mapping/" + source + ".json");
    }

    public static String[] toString(PagesESType... types) {
        return Arrays.stream(types).map(Enum::name).toArray(String[]::new);

    }

}
