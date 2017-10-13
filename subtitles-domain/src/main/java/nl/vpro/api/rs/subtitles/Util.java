package nl.vpro.api.rs.subtitles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.SubtitlesId;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class Util {

    public static Map<String, String> headers(SubtitlesId id, String extension) {
        Map<String, String> result = new HashMap<>();
        result.put("Content-Disposition", getContentDisposition(id, extension));
        result.put("X-subtitlesId", id.toString());
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
                    httpHeaders.putSingle("Content-Disposition", "inline; filename=" + head.getParent() + "." + "." + extension);
                } else {

                }
            }
        }
        return peeking;
    }
}
