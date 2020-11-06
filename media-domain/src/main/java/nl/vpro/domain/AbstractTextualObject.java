package nl.vpro.domain;

import lombok.Getter;

import org.meeuw.functional.TriFunction;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public abstract class AbstractTextualObject<T extends OwnedText, D extends OwnedText, TO extends AbstractTextualObject<T, D, TO>>
    extends AbstractTextualObjectUpdate<T, D, TO>
    implements TextualObject<T, D, TO> {

    @Getter
    private final TriFunction<String, OwnerType, TextualType, T> ownedTitleCreator;
    @Getter
    private final TriFunction<String, OwnerType, TextualType, D> ownedDescriptionCreator;


    public AbstractTextualObject(
        TriFunction<String, OwnerType, TextualType, T> ownedTitleCreator,
        TriFunction<String, OwnerType, TextualType, D> ownedDescriptionCreator) {
        super(
            (t, s) -> ownedTitleCreator.apply(t, DEFAULT_OWNER, s),
            (t, s) -> ownedDescriptionCreator.apply(t, DEFAULT_OWNER, s)
        );
        this.ownedTitleCreator = ownedTitleCreator;
        this.ownedDescriptionCreator = ownedDescriptionCreator;
    }
}
