package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.i18n.Displayable;


@XmlEnum
@XmlType(name = "avTypeEnum")
public enum AVType implements Displayable {

    AUDIO {
        @Override
        public String getDisplayName() {
            return "Audio";
        }
    },
    VIDEO {
        @Override
        public String getDisplayName() {
            return "Video";
        }
    },
    MIXED {
        @Override
        public String getDisplayName() {
            return "Afwisselend";
        }
    };

    @Override
    public String toString() {
        return getDisplayName();
    }
}
