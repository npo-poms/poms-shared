package nl.vpro.poms.shared;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import nl.vpro.util.Pair;

/**
 * Maintains thread locals to
 * be able to set 'extra' response headers.
 *
 * @author Michiel Meeuwissen
 */
@Slf4j
public class ExtraHeaders {

    static final ThreadLocal<List<Pair<String, String>>> EXTRA_HEADERS = ThreadLocal.withInitial(ArrayList::new);
    static final ThreadLocal<Boolean> EXTRA_HEADERS_USED = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private  ExtraHeaders() {
    }

    /**
     * Returns a read-only view on the extra headers.
     */
    public static List<Pair<String, String>> get() {
        return Collections.unmodifiableList(EXTRA_HEADERS.get());
    }

    public static void markUsed() {
        EXTRA_HEADERS_USED.set(Boolean.TRUE);
    }

    public static void remove() {
        EXTRA_HEADERS.remove();
        EXTRA_HEADERS_USED.remove();
    }

    public static String warn(String message, Object... args) {
        return add(Headers.NPO_WARNING_HEADER, message, args);
    }

    public static String add(String header, String message, Object... args) {
        if (EXTRA_HEADERS_USED.get()) {
            log.warn("Headers already used");
        }
        FormattingTuple ft = MessageFormatter.arrayFormat(message, args);
        String m = ft.getMessage();
        EXTRA_HEADERS.get().add(Pair.of(header, m));
        return m;
    }

}
