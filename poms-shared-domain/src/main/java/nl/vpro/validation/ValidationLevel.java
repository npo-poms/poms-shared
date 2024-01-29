package nl.vpro.validation;

import lombok.Getter;

import jakarta.validation.groups.Default;

import nl.vpro.i18n.Displayable;

/**
 * @since 7.7
 */
public enum ValidationLevel implements Displayable {

    /**
     *  Some fields are filled automatically, e.g. 'mid'. But they are required on persistance.
     *  This is the validation used by hibernate.
     * @since 7.10
     */
    PERSIST(Default.class, PrePersistValidatorGroup.class) {
        @Override
        public boolean display() {
            return false;
        }
    },
    DEFAULT(Default.class),

    /**
     * Added extra validation for most incoming data. E.g. uri-fields should be valid, and data will be rejected if not so.
     * Note, the data from what's on it too sloppy, and will just be validated with {@link #DEFAULT}
     */
    POMS(Default.class, PomsValidatorGroup.class),

    /**
     * Added extra validation for most incoming data. E.g. age rating must be filled. But provisionally non-fatal.
     */
    WARNING(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class),

    /**
     * A second layer of added extra validation for most incoming data. E.g. age rating must be filled. But provisionally non-fatal.
     */
    WEAK_WARNING(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class, WeakWarningValidatorGroup.class),

    /**
     * Validates every group, even the redundant ones. Some validation annotation are clarifying, but not really necessary, because would also be covered by another annotation.
     */
    REDUNDANT(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class, WeakWarningValidatorGroup.class, RedundantValidatorGroup.class) {
        @Override
        public boolean display() {
            return false;
        }
    };


    @Getter
    private final Class<?>[] classes;

    ValidationLevel(Class<?>... classes) {
        this.classes = classes;
    }
}
