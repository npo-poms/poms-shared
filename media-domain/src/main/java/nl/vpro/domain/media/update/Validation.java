package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import javax.validation.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@Slf4j
public class Validation {

    static final Validator VALIDATOR;

    static {
        Validator validator;
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        try (
            ValidatorFactory factory = javax.validation.Validation
                .buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        } catch (ValidationException ve) {
            log.info(ve.getClass().getName() + " " + ve.getMessage());
            validator = null;

        } catch(Throwable e) {
            // like java.lang.NoSuchMethodError: javax.el.ELUtil.getExpressionFactory()Ljavax/el/ExpressionFactory;
            log.warn(e.getClass().getName() + " " + e.getMessage());
            log.info("javax.validation will be disabled");
            validator = null;
        } finally {
            Locale.setDefault(defaultLocale);
        }
        VALIDATOR = validator;
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        if (VALIDATOR != null) {
            return VALIDATOR.validate(object, groups);
        } else {
            log.info("Cannot validate since no validator available");
            return Collections.emptySet();
        }
    }
}
