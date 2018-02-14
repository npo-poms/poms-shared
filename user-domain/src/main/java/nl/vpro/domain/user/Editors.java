package nl.vpro.domain.user;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Slf4j
public class Editors {

    private static Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();


    public static Editor mapWorksFor( List<Organization> organizations, Editor user, Function<Organization, Organization> consumer) {
        for (Organization organization : organizations) {
            if (! VALIDATOR.validate(organization).isEmpty()) {
                log.warn("The organization {} for {} is not valid. Ignoring", organization, user);
                continue;
            }

            if (organization.getDisplayName() == null) {
                organization.setDisplayName(organization.getId());
            }
            if (organization instanceof Broadcaster) {
                Broadcaster existing = (Broadcaster) consumer.apply(organization);
                if (existing != null) {
                    user.addBroadcaster(existing);
                } else {
                    log.warn("Cannot add broadcaster {} to user {} because this broadcaster does not exist", organization, user);
                }
            } else if (organization instanceof Portal) {
                Portal portal = (Portal) consumer.apply(organization);
                user.addPortal(portal);
            } else if (organization instanceof ThirdParty) {
                ThirdParty thirdParty = (ThirdParty) consumer.apply(organization);
                user.addThirdParty(thirdParty);
            } else {
                throw new RuntimeException("Unknown organization type: " + organization);
            }
        }

        log.debug("User works for : {}", user.getOrganizations());

        return user;
    }
}
