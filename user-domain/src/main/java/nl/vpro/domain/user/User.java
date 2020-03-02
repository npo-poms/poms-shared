package nl.vpro.domain.user;

import java.time.Instant;

import nl.vpro.domain.Identifiable;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public interface User extends Identifiable<String> {
    String getPrincipalId();
    String getGivenName();
    String getFamilyName();
    String getDisplayName();
    String getEmail();
    Instant getLastLogin();
    void setLastLogin(Instant instant);
}
