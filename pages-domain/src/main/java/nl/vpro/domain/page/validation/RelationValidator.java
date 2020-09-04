package nl.vpro.domain.page.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
            RelationDefinitionService broadcasterService = RelationDefinitionServiceProvider.getInstance();
            String bc = relationUpdate.getBroadcaster();
            String type = relationUpdate.getType();
            return broadcasterService.get(type, new Broadcaster(bc)) != null;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return true;
        }

    }

}
