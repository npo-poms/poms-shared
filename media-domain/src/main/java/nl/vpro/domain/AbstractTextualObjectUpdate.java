package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public abstract class AbstractTextualObjectUpdate<T extends TypedText, D extends TypedText, TO extends AbstractTextualObjectUpdate<T, D, TO>>
    implements TextualObjectUpdate<T, D, TO> {


    @Getter
    @Setter
    private SortedSet<T> titles = new TreeSet<>();


    @Getter
    @Setter
    private SortedSet<D> descriptions = new TreeSet<>();
}
