package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class BasicOwnedText extends BasicTypedText implements OwnedText {

    @Getter
    @Setter
    private OwnerType owner;

    public BasicOwnedText() {

    }

    public BasicOwnedText(String text, OwnerType owner, TextualType type) {
        super(text, type);
        this.owner = owner;
    }

    @Override
    public String toString() {
        return getType() + ":" + owner + ":" + get();
    }

}
