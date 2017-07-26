package nl.vpro.domain;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class BasicTextualObject extends AbstractTextualObject<BasicOwnedText, BasicOwnedText, BasicTextualObject> {

    @Override
    public BasicTextualObject addTitle(String title, OwnerType owner, TextualType type) {
        getTitles().add(new BasicOwnedText(owner, type, title));
        return self();

    }

    @Override
    public BasicTextualObject addDescription(String description, OwnerType owner, TextualType type) {
        getDescriptions().add(new BasicOwnedText(owner, type, description));
        return self();
    }

}
