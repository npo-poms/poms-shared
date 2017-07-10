package nl.vpro.domain;

import javax.annotation.Nonnull;

import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class BasicTextualUpdate extends AbstractTextualObjectUpdate<BasicTypedText, BasicTypedText, BasicTextualUpdate> {

    @Override
    public BasicTextualUpdate addTitle(String title, @Nonnull TextualType type) {
        getTitles().add(new BasicTypedText(type, title));
        return self();

    }

    @Override
    public BasicTextualUpdate addDescription(String description, @Nonnull TextualType type) {
        getDescriptions().add(new BasicTypedText(type, description));
        return self();
    }
}
