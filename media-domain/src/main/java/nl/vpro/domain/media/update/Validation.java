package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

import jakarta.validation.*;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.groups.Default;
import jakarta.validation.metadata.BeanDescriptor;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ExpressionLanguageFeatureLevel;

import com.google.common.cache.*;

import nl.vpro.i18n.Locales;
import nl.vpro.validation.*;

import static jakarta.validation.Validation.byProvider;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
@Slf4j
public class Validation {

    private static final LoadingCache<Locale, Validator> VALIDATORS = CacheBuilder
        .newBuilder().build(new CacheLoader<>() {
            @Override
            public @NonNull Validator load(@NonNull Locale key) throws Exception {
                return Validation.createValidator(key);
            }
        });


    public static final Class<?>[] DEFAULT_GROUPS = ValidationLevel.POMS.getClasses();


    private static Validator createValidator(Locale locale) {
        Validator validator;
        Locale defaultLocale = Locale.getDefault();
        try (
            ValidatorFactory factory = byProvider(HibernateValidator.class)
                .configure()
                .defaultLocale(locale)
                .customViolationExpressionLanguageFeatureLevel(ExpressionLanguageFeatureLevel.DEFAULT ) // makes ${validatedValue} work for custom violations
                .buildValidatorFactory()) {
            validator = factory.getValidator();
        } catch (ValidationException ve) {
            log.info(ve.getClass().getName() + " " + ve.getMessage());
            validator = null;

        } catch(Throwable e) {
            // like java.lang.NoSuchMethodError: javax.el.ELUtil.getExpressionFactory()Ljavax/el/ExpressionFactory;
            log.warn(e.getClass().getName() + " " + e.getMessage());
            validator = null;
        } finally {
            Locale.setDefault(defaultLocale);
        }
        if (validator == null) {
            log.info("No validator could be constructed for ({}). jakarta.validation will be disabled", locale);
            return new Validator() {
                @Override
                public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
                    return Collections.emptySet();
                }

                @Override
                public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
                    return Collections.emptySet();
                }

                @Override
                public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
                    return Collections.emptySet();
                }

                @Override
                public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
                    return null;
                }

                @Override
                public <T> T unwrap(Class<T> type) {
                    throw new ValidationException();
                }

                @Override
                public ExecutableValidator forExecutables() {
                    return null;
                }
            };
        }
        return validator;
    }

    public static Validator getValidator() {
        return getValidator(Locales.getDefault());
    }

    public static Validator getValidator(Locale locale) {
        return VALIDATORS.getUnchecked(locale);
    }


    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validate(object, DEFAULT_GROUPS);
    }

    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName) {
        return validateProperty(object,  propertyName,Default.class, PomsValidatorGroup.class, WarningValidatorGroup.class);
    }

    public static <T, E> Set<ConstraintViolation<E>> validateCollectionProperty(T object, Function<T, Collection<E>> getter) {
        Set<ConstraintViolation<E>> result = new LinkedHashSet<>();
        for (E e : getter.apply(object)) {
            Set<ConstraintViolation<E>> violations = validate(e);
            result.addAll(violations);
        }
        return result;
    }

    public static <T, E> void throwingValidateCollectionProperty(T object, Function<T, Collection<E>> getter) throws ConstraintViolationException {
        Set<ConstraintViolation<E>> validate = validateCollectionProperty(object, getter);
         if (!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }
    }

    public static <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return getValidator().validate(object, groups);
    }

    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
      return getValidator().validateProperty(object, propertyName, groups);
    }
}
