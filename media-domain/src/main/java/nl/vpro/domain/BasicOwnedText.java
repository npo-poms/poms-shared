package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class BasicOwnedText extends BasicTypedText implements OwnedText<BasicOwnedText> {

    @Getter
    @Setter
    private OwnerType owner;

    public BasicOwnedText() {

    }

    public BasicOwnedText(OwnerType owner, TextualType type, String text) {
        super(type, text);
        this.owner = owner;
    }

    @Override
    public int compareTo(BasicOwnedText o) {
        return super.compareTo(o);
    }
}
