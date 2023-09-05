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
    /**
     * For groups this means that it contains both audio and video.
     * <p>
     * For a playable object, it may mean that it contains both audio and video {@link Location locations}. They should represent the same content though. E.g. 'visual radio'.
     */
    MIXED {
        @Override
        public String getDisplayName() {
            return "Afwisselend/Beide";
        }
    };

    @Override
    public String toString() {
        return getDisplayName();
    }
}
