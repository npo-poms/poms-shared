package nl.vpro.domain.media;

import java.util.function.Predicate;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import org.meeuw.functional.Predicates;

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
    AUDIO(ANY_MEDIA, mt -> ! MediaType.VISUALS.contains(mt)) {
        @Override
        public String getDisplayName() {
            return "Audio";
        }
    },

    VIDEO(ANY_MEDIA, Predicates.alwaysTrue()) {
        @Override
        public String getDisplayName() {
            return "Video";
        }
    },

    /**
     * It is (as yet) unknown if the object will be {@link #VIDEO} or {@link #AUDIO}
     * @since 7.8
     */
    UNKNOWN(ANY_MEDIA, Predicates.alwaysTrue()) {
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
    MIXED(GROUPS, Predicates.alwaysTrue()) {
        @Override
        public String getDisplayName() {
            return "Afwisselend";
        }
    };


    private final Predicate<Class<?>> predicate;

    private final Predicate<MediaType> mediaTypePredicate;


    AVType(Predicate<Class<?>> predicate, Predicate<MediaType> mediaTypePredicate) {
        this.predicate = predicate;
        this.mediaTypePredicate = mediaTypePredicate;
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
            return testClass(clazz);
        }
        if (mediaObject == null) {
            return false;
        }
        if (mediaObject instanceof MediaType mt) {
            return mediaTypePredicate.test(mt) && testClass(mt.getMediaClass());
        }
        Class<?> clazz = mediaObject.getClass();
        if (!testClass(clazz)) {
            return false;
        }
        if (mediaObject instanceof MediaUpdate<?> mediaUpdate) {
            return mediaTypePredicate.test(mediaUpdate.getMediaType());
        }
        if (mediaObject instanceof MediaObject media) {
            return mediaTypePredicate.test(media.getMediaType());
        }
        return false;
    }


    private boolean testClass(Object mediaObject) {
        if (mediaObject instanceof Class<?> clazz) {
            return predicate.test(clazz);
        }

        return mediaObject != null && predicate.test(mediaObject.getClass());
    }


}
