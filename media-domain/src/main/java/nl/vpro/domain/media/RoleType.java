package nl.vpro.domain.media;

import lombok.Getter;

import java.util.Optional;

import jakarta.xml.bind.annotation.XmlEnum;

import nl.vpro.i18n.*;

/**
 * How {@link Credits persons} are related to {@link MediaObject media objects}, what their relation is to some media.
 */
@XmlEnum
public enum RoleType  implements Displayable {

    DIRECTOR("Regisseur", "Regie", "Regisseur"),
    CHIEF_EDITOR("Eindredactie", "EindRedactie", "Eindredacteur"),
    EDITOR("Redacteur" , "Redactie", "Redacteur"),
    PRESENTER("Presentator", "Presentatie", "Presentator"),
    INTERVIEWER("Interviewer", "Interview", "Interviewer"),
    PRODUCER("Productie", "Productie", "Producer"),
    RESEARCH("Research", "Research", "Researcher"),
    GUEST("Gast", "Te gast", "Gast"),
    REPORTER("Verslaggever", "Verslaggeving", "Verslaggever"),
    ACTOR("Acteur", "Acteerwerk", "Acteur"),
    COMMENTATOR("Commentaar", "Commentaar", "Commentator"),
    COMPOSER("Componist", "Compositie", "Componist"),
    SCRIPTWRITER("Scenario", "Scenario", "Scenarist"),
    /**
     * For radio shows this seems useful
     * @since 5.8
     */
    NEWS_PRESENTER("Nieuwslezer", "Nieuwslezing", "Nieuwslezer"),
     /**
     * For radio shows this seems useful
     * @since 5.9
     */
    SIDEKICK("Sidekick", "Sidekick", "Sidekick"),
    /**
     * <a href="https://jira.vpro.nl/browse/MSE-4371">jira</a>
     * @since 5.11
     */
    SUBJECT("Onderwerp", "Onderwerp", "Onderwerp"),
    /**
     * See <a href="https://jira.vpro.nl/browse/MSE-4371">jira</a>
     * @since 5.11
     */
    PARTICIPANT("Deelnemer", "Deelname", "Deelnemer"),


    /**
     * Supported by bindinc
     * @since 5.20
     */
    ASSISTANT_DIRECTOR("Assistent-regisseur", "Regieassistentie", "Assistent-regisseur", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    CAMERA("Camerawerk", "Camerawerk", "Cameraman", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    CHOREOGRAPHY("Choreografie", "Choreografie", "Choreograaf", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    DUBBING("Ondertiteling", "Ondertiteling", "Ondertitelaar", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    MAKEUP("Make-up", "Make-up", "Grimeur", false),

    /**
     * Supported by bindinc
     * @since 5.20
     */
    PRODUCTION_MANAGEMENT("Productie-management", "Productie-management", "Productie-manager", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    STAGING("Staging", "Staging", "Setdresser", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    STUNT("Stunts", "Stunts", "Stuntman", false),
    /**
     * Supported by bindinc
     * @since 5.20
     */
    VISUAL_EFFECTS("Visual Effects", "Visual effects", "Special effects editor", false),

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


    public static RoleType fromToString(String string) {
        for (RoleType role : RoleType.values()) {
            if (string.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
