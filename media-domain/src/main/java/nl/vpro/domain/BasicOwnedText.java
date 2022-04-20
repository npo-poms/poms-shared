package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * {@inheritDoc}
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class BasicOwnedText extends BasicTypedText implements OwnedText {

    private static final long serialVersionUID = -6428591725078554520L;

    @Getter
    @Setter
    private OwnerType owner;

    public BasicOwnedText() {

    }

    public BasicOwnedText(String text, OwnerType owner, TextualType type) {
        super(text, type);
        this.owner = owner;
    }



}
