package nl.vpro.domain.user;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import jakarta.validation.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class Editors {

    private Editors() {
    }

    private static final Validator VALIDATOR;

    static {
        Validator validator;
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        } catch (Exception e) {
            log.warn(e.getMessage());
            validator  = null;
        }
        VALIDATOR = validator;
    }


    public static Editor mapWorksFor(List<Organization> organizations, Editor user) {
        if (organizations != null) {
            for (Organization organization : organizations) {
                if (VALIDATOR != null && !VALIDATOR.validate(organization).isEmpty()) {
                    log.warn("The organization {} for {} is not valid. Ignoring", organization, user);
                    continue;
                }

                if (organization.getDisplayName() == null) {
                    organization.setDisplayName(organization.getId());
                }
                user.addOrganization(organization);
            }
            log.debug("User works for : {}", user.getOrganizations());
        }
        return user;
    }
}
