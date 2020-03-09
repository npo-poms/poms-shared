package nl.vpro.domain.user;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class Editors {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();


    public static Editor mapWorksFor( List<Organization> organizations, Editor user) {
        if (organizations != null) {
            for (Organization organization : organizations) {
                if (!VALIDATOR.validate(organization).isEmpty()) {
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
