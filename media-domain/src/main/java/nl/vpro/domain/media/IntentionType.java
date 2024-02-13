package nl.vpro.domain.media;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.i18n.Displayable;


/**
 * @author Giorgio Vinci
 * @since 5.11
 */
@XmlEnum
@XmlType(name = "intentionEnum")
public enum IntentionType implements Displayable {
    INFORM {
        @Override
        public String getDisplayName() {
            return "Informeren";
        }
    },
    INFORM_NEWS_AND_FACTS {
        @Override
        public String getDisplayName() {
            return "Informeren / verdiepen - nieuws en actualiteiten";
        }
    },
    INFORM_INDEPTH {
        @Override
        public String getDisplayName() {
            return "Informeren / verdiepen - zwaar informatief";
        }
    },
    INFORM_GENERAL {
        @Override
        public String getDisplayName() {
            return "Informeren / verdiepen - licht informatief";
        }
    },
    ENTERTAINMENT {
        @Override
        public String getDisplayName() {
            return "Vermaken";
        }
    },
    ENTERTAINMENT_LEASURE {
        @Override
        public String getDisplayName() {
            return "Vermaken - Puur vermaak";
        }
    },
    ENTERTAINMENT_INFORMATIVE {
        @Override
        public String getDisplayName() {
            return "Informatief vermaak";
        }
    },
    ACTIVATING {
        @Override
        public String getDisplayName() {
            return "Activeren";
        }
    };

    @Override
    public String toString() {
        return getDisplayName();
    }
}
