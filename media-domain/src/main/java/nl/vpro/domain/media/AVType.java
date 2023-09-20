package nl.vpro.domain.media;

import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.i18n.Displayable;

import static nl.vpro.domain.media.MediaObjects.ANY_MEDIA;
import static nl.vpro.domain.media.MediaObjects.GROUPS;


@XmlEnum
@XmlType(name = "avTypeEnum")
public enum AVType implements Displayable, Predicate<Object> {



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
     * For groups this means that it contains both audio and video.
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
