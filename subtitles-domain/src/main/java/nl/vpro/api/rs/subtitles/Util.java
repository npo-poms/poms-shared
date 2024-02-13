package nl.vpro.api.rs.subtitles;

import java.util.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MultivaluedMap;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import nl.vpro.domain.subtitles.*;
import nl.vpro.poms.shared.Headers;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class Util {

    private Util() {
    }

    public static Map<String, String> headers(SubtitlesId id, String extension) {
        Map<String, String> result = new HashMap<>();
        result.put("Content-Disposition", getContentDisposition(id, extension));
        result.put(Headers.NPO_SUBTITLES_ID, id.toString().replaceAll("\\s+", " "));
        return result;

    }
    public static void headers(SubtitlesId id, String extension, HttpServletResponse response) {
        for (Map.Entry<String, String> e : Util.headers(id, extension).entrySet()) {
            response.addHeader(e.getKey(), e.getValue());
        }
    }
    static void headers(SubtitlesId id, MultivaluedMap<String, Object> httpHeaders, String extension) {
        for(Map.Entry<String, String> e : headers(id, extension).entrySet()) {
            httpHeaders.putSingle(e.getKey(), e.getValue());
        }
    }

    static String getContentDisposition(SubtitlesId id, String extension) {
        // Sadly inline with filename is not support any more by chrome
        // It is indeed not in the specs that it should.
        // We could make it 'attachment', but then it won't show in the browser.
        // I liked it that you could specify a better filename for inline content which was saved to disk anyway.
        return "inline; filename=\"" + id.getMid() + "." + id.getLanguage() + "." + extension + "\"";
    }

    static Iterator<Cue> headers(Iterator<Cue> cueIterator, MultivaluedMap<String, Object> httpHeaders, String extension) {
        PeekingIterator<Cue> peeking = Iterators.peekingIterator(cueIterator);
        if (peeking.hasNext()) {
            Cue head = peeking.peek();
            if (head instanceof StandaloneCue) {
                SubtitlesId id = ((StandaloneCue) head).getSubtitlesId();
                headers(id, httpHeaders, extension);
            } else {
                if (head != null) {
                    httpHeaders.putSingle("Content-Disposition", "attachment; filename=" + head.getParent() + "." + "." + extension);
                } else {

                }
            }
        }
        return peeking;
    }
}
