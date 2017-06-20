package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(name = "avTypeEnum")
public enum AVType {

    AUDIO {
        @Override
        public String toString() {
            return "Audio";
        }
    },
    VIDEO {
        @Override
        public String toString() {
            return "Video";
        }
    },
    MIXED {
        @Override
        public String toString() {
            return "Afwisselend";
        }
    }
}
