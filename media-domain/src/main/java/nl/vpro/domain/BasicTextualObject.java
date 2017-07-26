package nl.vpro.domain;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.util.TriFunction;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class BasicTextualObject extends AbstractTextualObject<BasicOwnedText, BasicOwnedText, BasicTextualObject> {

    @Override
    public TriFunction<String, OwnerType, TextualType, BasicOwnedText> getOwnedTitleCreator() {
        return BasicOwnedText::new;
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, BasicOwnedText> getOwnedDescriptionCreator() {
        return BasicOwnedText::new;
    }

}
