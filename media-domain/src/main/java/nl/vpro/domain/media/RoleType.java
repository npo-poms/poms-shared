package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

import nl.vpro.domain.Displayable;

@XmlEnum
public enum RoleType  implements Displayable {

    DIRECTOR {

        @Override
        public String toString() {
            return "Regisseur";
        }
    },
    CHIEF_EDITOR {

        @Override
        public String toString() {
            return "Eindredactie";
        }
    },
    EDITOR {

        @Override
        public String toString() {
            return "Redacteur";
        }
    },
    PRESENTER {

        @Override
        public String toString() {
            return "Presentator";
        }
    },
    INTERVIEWER {

        @Override
        public String toString() {
            return "Interviewer";
        }
    },
    PRODUCER {

        @Override
        public String toString() {
            return "Productie";
        }
    },
    RESEARCH {

        @Override
        public String toString() {
            return "Research";
        }
    },
    GUEST {

        @Override
        public String toString() {
            return "Gast";
        }
    },
    REPORTER {

        @Override
        public String toString() {
            return "Verslaggever";
        }
    },
    ACTOR {

        @Override
        public String toString() {
            return "Acteur";
        }
    },
    COMMENTATOR {

        @Override
        public String toString() {
            return "Commentaar";
        }
    },
    COMPOSER {

        @Override
        public String toString() {
            return "Componist";
        }
    },
    SCRIPTWRITER {

        @Override
        public String toString() {
            return "Scenario";
        }
    },
    /**
     * For radio shows this seems useful
     * @since 5.8
     */
    NEWS_PRESENTER {
        @Override
        public String toString() {
            return "Nieuwslezer";
        }

    },
     /**
     * For radio shows this seems useful
     * @since 5.9
     */
    SIDEKICK {
        @Override
        public String toString() {
            return "Sidekick";
        }

    },
    /**
     * https://jira.vpro.nl/browse/MSE-4371
     * @since 5.11
     */
    SUBJECT {
        @Override
        public String toString() {
            return "Onderwerp";
        }
    },
    /**
     * See https://jira.vpro.nl/browse/MSE-4371
     * @since 5.11
     */
    PARTICIPANT {
        @Override
        public String toString() {
            return "Deelnemer";
        }
    },
    UNDEFINED {
        @Override
        public String toString() {
            return "Overig";
        }
    };

    public static RoleType fromToString(String string) {
        for (RoleType role : RoleType.values()) {
            if (string.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException("No existing value: " + string);
    }

}
