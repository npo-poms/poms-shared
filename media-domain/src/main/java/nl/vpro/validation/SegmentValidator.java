package nl.vpro.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.Segment;

/**
 * @author Michiel Meeuwissen
 * @since 2.3.1
 */
public class SegmentValidator implements ConstraintValidator<SegmentValidation, Segment> {

    @Override
    public boolean isValid(Segment value, ConstraintValidatorContext context) {
        return value.getMidRef() != null || value.getParent() != null;
    }
}
