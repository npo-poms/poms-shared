package nl.vpro.domain.validation;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.support.Ownable;
import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class NoDuplicateOwnerValidator implements ConstraintValidator<NoDuplicateOwner, Collection<? extends Ownable>> {


    @Override
    public boolean isValid(Collection<? extends Ownable> value, ConstraintValidatorContext context) {
        return isValid(value);
    }


    protected  boolean isValid(Collection<? extends Ownable> value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        Map<OwnerType, AtomicInteger> counts = new HashMap<>();
        for (Ownable o : value) {
            if (counts.computeIfAbsent(o.getOwner(), (a) -> new AtomicInteger(0)).incrementAndGet() > 1) {
                return false;
            }

        }
        return true;

    }
}
