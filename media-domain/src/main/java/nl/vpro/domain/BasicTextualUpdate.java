package nl.vpro.domain;

import java.util.function.BiFunction;

import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class BasicTextualUpdate extends AbstractTextualObjectUpdate<BasicTypedText, BasicTypedText, BasicTextualUpdate> {

    @Override
    public BiFunction<String, TextualType, BasicTypedText> getTitleCreator() {
        return BasicTypedText::new;
    }

    @Override
    public BiFunction<String, TextualType, BasicTypedText> getDescriptionCreator() {
        return BasicTypedText::new;
    }
}
