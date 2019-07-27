package nl.vpro.domain.media.support;

import java.util.Iterator;
import java.util.List;

import nl.vpro.domain.Child;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface OwnableList<THIS, I extends Comparable<I> & Child<THIS>> extends Ownable, Comparable<THIS>, Iterable<I> {


    List<I> getValues();

    @Override
    default Iterator<I> iterator() {
        return getValues().iterator();
    }

}
