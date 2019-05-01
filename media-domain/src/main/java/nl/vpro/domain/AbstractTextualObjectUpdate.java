package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;

import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public abstract class AbstractTextualObjectUpdate<T extends TypedText, D extends TypedText, TO extends AbstractTextualObjectUpdate<T, D, TO>>
    implements TextualObjectUpdate<T, D, TO> {

    @Getter
    private final BiFunction<String, TextualType, T> titleCreator;
    @Getter
    private final BiFunction<String, TextualType, D> descriptionCreator;


    @Getter
    @Setter
    private SortedSet<T> titles = new TreeSet<>();


    @Getter
    @Setter
    private SortedSet<D> descriptions = new TreeSet<>();

    protected AbstractTextualObjectUpdate(BiFunction<String, TextualType, T> titleCreator,
                                          BiFunction<String, TextualType, D> descriptionCreator) {
        this.titleCreator = titleCreator;
        this.descriptionCreator = descriptionCreator;
    }

    @Override
    public String toString() {
        return getMainTitle() + " " + getMainDescription();
    }
}
