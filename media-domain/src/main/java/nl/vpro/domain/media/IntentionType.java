package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "intentionEnum")
public enum IntentionType {
    INFORM_NEWS_AND_FACTS {
        @Override
        public String toString() {
            return "Informeren /verdiepen, nieuws en actualiteiten";
        }
    },
    INFORM_INDEPTH {
        @Override
        public String toString() {
            return "Informeren /verdiepen, zwaar informatief";
        }
    },
    INFORM_GENERAL {
        @Override
        public String toString() {
            return "Informeren /verdiepen, licht informatief";
        }
    },
    ENTERTAINMENT_LEASURE {
        @Override
        public String toString() {
            return "Vermaken, Puur vermaak";
        }
    },
    ENTERTAINMENT_INFORMATIVE {
        @Override
        public String toString() {
            return "Informatief vermaak";
        }
    },
    ACTIVATING {
        @Override
        public String toString() {
            return "Activeren";
        }
    }
}
