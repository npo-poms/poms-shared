package nl.vpro.domain;

import java.time.Instant;

import nl.vpro.domain.user.Editor;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class Accountables {

    public static void fillFor(Accountable accountable, Instant now, Editor currentUser) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
        accountable.setLastModifiedBy(currentUser);
        if (accountable.getCreatedBy() == null) {
            accountable.setCreatedBy(currentUser);
        }
    }
}
