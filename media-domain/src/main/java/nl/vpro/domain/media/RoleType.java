package nl.vpro.domain.media;

import lombok.Getter;

import javax.xml.bind.annotation.XmlEnum;

import nl.vpro.domain.Displayable;

@XmlEnum
public enum RoleType  implements Displayable {

    DIRECTOR("Regisseur"),
    CHIEF_EDITOR("Eindredactie"),
    EDITOR("Redacteur"),
    PRESENTER("Presentator"),
    INTERVIEWER("Interviewer"),
    PRODUCER("Productie"),
    RESEARCH("Research"),
    GUEST("Gast"),
    REPORTER("Verslaggever"),
    ACTOR("Acteur"),
    COMMENTATOR("Commentaar"),
    COMPOSER("Componist"),
    SCRIPTWRITER("Scenario"),
    /**
     * For radio shows this seems useful
     * @since 5.8
     */
    NEWS_PRESENTER("Nieuwslezer"),
     /**
     * For radio shows this seems useful
     * @since 5.9
     */
    SIDEKICK("Sidekick"),
    /**
     * https://jira.vpro.nl/browse/MSE-4371
     * @since 5.11
     */
    SUBJECT("Onderwerp"),
    /**
     * See https://jira.vpro.nl/browse/MSE-4371
     * @since 5.11
     */
    PARTICIPANT("Deelnemer"),
    UNDEFINED("Overig")
    ;

    @Getter
    private final String displayName;

    RoleType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
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
