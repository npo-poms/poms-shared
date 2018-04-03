package nl.vpro.domain.media;

import lombok.Getter;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public enum Encryption implements Displayable {
    NONE("Geen", true),
    DRM("DRM", true),
    UNDETERMINED("Onbepaald", false);

    @Getter
    private final String displayName;

    @Getter
    private final boolean inGui;

    Encryption(String displayName, boolean inGui) {
        this.displayName = displayName;
        this.inGui = inGui;
    }
}
