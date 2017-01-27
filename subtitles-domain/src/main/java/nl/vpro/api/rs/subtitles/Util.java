package nl.vpro.api.rs.subtitles;

import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.util.CountedIterator;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
class Util {

    static Iterator<StandaloneCue> headers(CountedIterator<StandaloneCue> cueIterator, MultivaluedMap<String, Object> httpHeaders, String extension) {
        Iterator<StandaloneCue> i = cueIterator;
        if (cueIterator.getTotalSize().orElse(0L) > 0L) {
            PeekingIterator<StandaloneCue> peeking = Iterators.peekingIterator(cueIterator);
            i = peeking;
            StandaloneCue head = peeking.peek();
            httpHeaders.putSingle("Content-Disposition", "inline; fileName=" + head.getParent() + "." + head.getLocale().toString() + "." + extension + ";");
        }
        return i;
    }
}
