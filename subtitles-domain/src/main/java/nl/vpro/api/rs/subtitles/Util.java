package nl.vpro.api.rs.subtitles;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

     static void headers(SubtitlesId id, MultivaluedMap<String, Object> httpHeaders, String extension) {
        for(Map.Entry<String, String> e : headers(id, extension).entrySet()) {
            httpHeaders.putSingle(e.getKey(), e.getValue());
        }

    }

    static String getContentDisposition(SubtitlesId id, String extension) {
        return "inline; fileName=" + id.getMid() + "." + id.getLanguage() + "." + extension + ";";
    }

    static Iterator<Cue> headers(Iterator<Cue> cueIterator, MultivaluedMap<String, Object> httpHeaders, String extension) {


        PeekingIterator<Cue> peeking = Iterators.peekingIterator(cueIterator);
        if (peeking.hasNext()) {
            Cue head = peeking.peek();
            if (head instanceof StandaloneCue) {
                httpHeaders.putSingle("Content-Disposition",  getContentDisposition(((StandaloneCue) head).getSubtitlesId(), extension));
            } else {
                httpHeaders.putSingle("Content-Disposition", "inline; fileName=" + head.getParent() + "." + "." + extension + ";");
            }
        }
        return peeking;
    }
}
