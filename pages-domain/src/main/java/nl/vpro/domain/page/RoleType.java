package nl.vpro.domain.page;

import lombok.Getter;

import java.util.Optional;

import nl.vpro.i18n.*;

/**
 * Role types for {@link Credits credits} in relation to {@link Page pages}
 * @since 7.7
 * @see nl.vpro.domain.media.RoleType
 */
public enum RoleType implements Displayable {

    AUTHOR("Auteur", "Auteur", "Auteur"),
    SUBJECT("Onderwerp", "Onderwerp", "Onderwerp"),
    UNDEFINED("Overig", "Overig", "Overig")

    ;

    @Getter
    private final String string;

    private final String personFunction;

    @Getter
    private final String role;

    private final boolean display;

    RoleType(String string, String role, String personFunction) {
        this(string, role, personFunction, true);
    }

    RoleType(String string, String role, String personFunction, boolean display) {
        this.string = string;
        this.role = role;
        this.personFunction = personFunction;
        this.display = display;
    }
     @Override
    public String toString() {
        return string;
    }

    @Override
    public String getDisplayName() {
        return personFunction;
    }
    @Override
    public Optional<LocalizedString> getPluralDisplayName() {
        return Optional.of(LocalizedString.of(role, Locales.NETHERLANDISH));
    }

    @Override
    public boolean display() {
        return display;
    }


    public static nl.vpro.domain.media.RoleType fromToString(String string) {
        for (nl.vpro.domain.media.RoleType role : nl.vpro.domain.media.RoleType.values()) {
            if (string.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
