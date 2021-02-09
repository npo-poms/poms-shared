package nl.vpro.poms.shared;

import java.util.*;

import nl.vpro.util.Pair;

/**
 * @author Michiel Meeuwissen
 */
public class ExtraHeaders {

    static final ThreadLocal<List<Pair<String, String>>> EXTRA_HEADERS = ThreadLocal.withInitial(ArrayList::new);

    private  ExtraHeaders() {
    }

    public static List<Pair<String, String>> get() {
        return EXTRA_HEADERS.get();
    }

    public static void remove() {
        EXTRA_HEADERS.remove();
    }

    public static void warn(String message) {
        EXTRA_HEADERS.get().add(Pair.of(Headers.NPO_WARNING_HEADER, message));
    }
}
