package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Setter
@Getter
public class BasicOwnedText extends BasicTypedText implements OwnedText {

    @Serial
    private static final long serialVersionUID = -6428591725078554520L;

    private OwnerType owner;

    public BasicOwnedText() {

    }

    public BasicOwnedText(String text, OwnerType owner, TextualType type) {
        super(text, type);
        this.owner = owner;
    }



}
