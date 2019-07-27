package nl.vpro.domain.media.support;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.Child;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface OwnableList<THIS, I extends Comparable<I> & Child<THIS>> extends Ownable, Comparable<THIS>, Iterable<I> {


    @NonNull
    List<I> getValues();

    @Override
    @NonNull
    default Iterator<I> iterator() {
        return getValues().iterator();
    }

}
