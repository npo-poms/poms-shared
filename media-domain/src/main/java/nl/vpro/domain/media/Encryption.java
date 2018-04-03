package nl.vpro.domain.media;

import lombok.Getter;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.6.1
 */
public enum Encryption implements Displayable {
    NONE("Geen"),
    DRM("DRM");

    @Getter
    private final String displayName;

    Encryption(String displayName) {
        this.displayName = displayName;
    }
}
