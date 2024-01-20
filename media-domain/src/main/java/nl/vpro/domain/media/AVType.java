package nl.vpro.domain.media;

import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.i18n.Displayable;

import static nl.vpro.domain.media.MediaObjects.ANY_MEDIA;
import static nl.vpro.domain.media.MediaObjects.GROUPS;


/**
 * Audio/Video type.
 */
@XmlEnum
@XmlType(name = "avTypeEnum")
public enum AVType implements Displayable, Predicate<Object> {


    /**
     * Representing media with no video.
     */
    AUDIO(ANY_MEDIA) {
        @Override
        public String getDisplayName() {
            return "Audio";
        }
    },
    VIDEO(ANY_MEDIA) {
        @Override
        public String getDisplayName() {
            return "Video";
        }
    },

    /**
     * It is (as yet) unknown if the object will be {@link #VIDEO} or {@link #AUDIO}
     * @since 7.8
     */
    UNKNOWN(ANY_MEDIA) {
        @Override
        public String getDisplayName() {
            return "Onbekend";
        }
        @Override
        public boolean display() {
            return false;
        }
    },
    /**
     * For groups this means that its members can be both {@link #AUDIO audio} and {@link #VIDEO video}.
     */
    MIXED(GROUPS) {
        @Override
        public String getDisplayName() {
            return "Afwisselend";
        }
    };


    private final Predicate<Class<?>> predicate;

    AVType(Predicate<Class<?>> predicate) {
        this.predicate = predicate;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this case checks whether the given object may have this AVType.
     * @param mediaObject the object to check. A {@link Class}, a {@link MediaObject} or a {@link MediaUpdate}.
     * @see MediaObjects#GROUPS
     * @see MediaObjects#NO_GROUPS
     */
    @Override
    public boolean test(Object mediaObject) {
        if (mediaObject instanceof Class<?> clazz) {
            return predicate.test(clazz);
        }

        return mediaObject != null && predicate.test(mediaObject.getClass());
    }


}
