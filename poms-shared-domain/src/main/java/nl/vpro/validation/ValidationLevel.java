package nl.vpro.validation;

import lombok.Getter;

import javax.validation.groups.Default;

import nl.vpro.i18n.Displayable;

/**
 * @since 7.7
 */
public enum ValidationLevel implements Displayable {
   /* PERSIST(Default.class, PrePersistValidatorGroup.class) {
        @Override
        public boolean display() {
            return false;
        }
    },*/
    DEFAULT(Default.class),
    POMS(Default.class, PomsValidatorGroup.class),
    WARNING(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class),
    WEAK_WARNING(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class, WeakWarningValidatorGroup.class),
    /**
     * Validates every group, even the redundant ones.
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
