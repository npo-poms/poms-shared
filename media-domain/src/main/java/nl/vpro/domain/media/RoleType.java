package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum RoleType {
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
    PARTICIPANT {
        @Override
        public String toString() {
            return "Deelnemer";
        }
    },
    CHIEF_EDITOR {

        @Override
        public String toString() {
            return "Eindredactie";
        }
    },
    GUEST {

        @Override
        public String toString() {
            return "Gast";
        }
    },
    INTERVIEWER {

        @Override
        public String toString() {
            return "Interviewer";
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
    THEME {
        @Override
        public String toString() {
            return "Onderwerp";
        }
    },
    PRESENTER {

        @Override
        public String toString() {
            return "Presentator";
        }
    },
    PRODUCER {

        @Override
        public String toString() {
            return "Productie";
        }
    },
    EDITOR {

        @Override
        public String toString() {
            return "Redacteur";
        }
    },
    DIRECTOR {
        @Override
        public String toString() {
            return "Regisseur";
        }
    },

    RESEARCH {

        @Override
        public String toString() {
            return "Research";
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
     * @since 5.9
     */
    SIDEKICK {
        @Override
        public String toString() {
            return "Sidekick";
        }

    },
    REPORTER {

        @Override
        public String toString() {
            return "Verslaggever";
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
