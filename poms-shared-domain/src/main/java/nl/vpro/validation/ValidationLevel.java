package nl.vpro.validation;

import lombok.Getter;

import javax.validation.groups.Default;

/**
 * @since 7.7
 */
public enum ValidationLevel {
    DEFAULT(Default.class),
    POMS(Default.class, PomsValidatorGroup.class),
    WARNING(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class),
    WEAK_WARNING(Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class, WeakWarningValidatorGroup.class);

    @Getter
    private final Class<?>[] classes;

    ValidationLevel(Class<?>... classes) {
        this.classes = classes;
    }
}
