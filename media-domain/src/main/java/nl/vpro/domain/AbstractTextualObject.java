package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public abstract class AbstractTextualObject<T extends OwnedText, D extends OwnedText, TO extends AbstractTextualObject<T, D, TO>>
    extends AbstractTextualObjectUpdate<T, D, TO>
    implements TextualObject<T, D, TO> {


}
