package nl.vpro.api.rs.subtitles;

import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.SubtitlesId;
import nl.vpro.util.CountedIterator;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class Util {


    static void headers(SubtitlesId id, MultivaluedMap<String, Object> httpHeaders, String extension) {
        httpHeaders.putSingle("Content-Disposition", "inline; fileName=" + id.getMid() + "." +  id.getLanguage() + "." + extension + ";");
        httpHeaders.putSingle("X-subtitlesId", id.toString());

    }

    static Iterator<? extends Cue> headers(CountedIterator<? extends Cue> cueIterator, MultivaluedMap<String, Object> httpHeaders, String extension) {


        Iterator<? extends Cue> i = cueIterator;
        if (cueIterator.getTotalSize().orElse(0L) > 0L) {
            PeekingIterator<? extends Cue> peeking = Iterators.peekingIterator(cueIterator);
            i = peeking;
            Cue head = peeking.peek();
            if (head instanceof StandaloneCue) {
                httpHeaders.putSingle("Content-Disposition", "inline; fileName=" + head.getParent() + "." + ((StandaloneCue) head).getLocale().toString() + "." + extension + ";");
            } else {
                httpHeaders.putSingle("Content-Disposition", "inline; fileName=" + head.getParent() + "." + "." + extension + ";");
            }
        }
        return i;
    }
}
