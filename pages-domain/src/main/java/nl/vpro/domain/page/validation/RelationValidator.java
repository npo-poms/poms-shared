package nl.vpro.domain.page.validation;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import nl.vpro.domain.page.RelationDefinitionService;
import nl.vpro.domain.page.RelationDefinitionServiceProvider;
import nl.vpro.domain.page.update.RelationUpdate;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@Slf4j
public class RelationValidator implements ConstraintValidator<ValidRelation, RelationUpdate> {
    @Override
    public void initialize(ValidRelation constraintAnnotation){

    }

    @Override
    public boolean isValid(RelationUpdate relationUpdate, ConstraintValidatorContext context) {
        try {
            RelationDefinitionService relationDefinitionService = RelationDefinitionServiceProvider.getInstance();
            String bc = relationUpdate.getBroadcaster();
            String type = relationUpdate.getType();
            return relationDefinitionService.get(type, new Broadcaster(bc)) != null;
        } catch (IllegalStateException ise) {
            log.warn(ise.getMessage());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return true;
        }

    }

}
