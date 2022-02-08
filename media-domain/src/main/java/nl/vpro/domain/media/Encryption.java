package nl.vpro.domain.media;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.i18n.Displayable;


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

    public static Encryption valueOfOrNull(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return Encryption.valueOf(id);
    }
}
