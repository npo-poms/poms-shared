package nl.vpro.domain.media;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;

import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.countries.validation.ValidCountry;
import org.meeuw.i18n.regions.validation.ValidRegion;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * A meta annotation for country codes which are considered valid in poms.
 * <p>
 * These are all former, officially assigned and most known user assigned countries.
 * Additionally, the countries of UK are accepted (these are really subdivisions).
 *
 * @since 5.31
 */
@ValidRegion(classes = {Country.class}, includes = {"GB-ENG", "GB-NIR", "GB-SCT", "GB-WLS"})
@ValidCountry(value = ValidCountry.OFFICIAL | ValidCountry.USER_ASSIGNED | ValidCountry.FORMER,
    excludes = {"XN"} // patent institute assigned
)
@NotNull
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE_PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface PomsValidCountry {

    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
