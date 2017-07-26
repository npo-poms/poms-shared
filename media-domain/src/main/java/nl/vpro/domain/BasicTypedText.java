package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

import nl.vpro.domain.media.support.TextualType;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class BasicTypedText implements TypedText, Comparable<TypedText> {
    @Getter
    @Setter
    private TextualType type;

    private String text;

    public BasicTypedText() {

    }

    public BasicTypedText(String text, TextualType type) {
        this.type = type;
        this.text = text;
    }

    @Override
    public void set(String s) {
        this.text = s;

    }

    @Override
    public String get() {
        return text;

    }
    @Override
    public String toString() {
        return type + ":" + text;
    }

}
