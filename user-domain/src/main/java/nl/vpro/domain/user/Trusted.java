package nl.vpro.domain.user;

import java.util.Objects;

import static nl.vpro.domain.Roles.SUPERPROCESS_ROLE;
import static nl.vpro.domain.Roles.SYSTEM_ROLE;

/**
 * Representation of a 'trusted' user. Such a user needs not to exist in LDAP.
 *
 * Instances of this are only done explicitely in java.
 *
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public interface Trusted {

    String getPrincipal();

    String[] getRoles();

    static Trusted of(String principal, String... roles) {
        return new Trusted() {
            @Override
            public String getPrincipal() {
                return principal;

            }

            @Override
            public String[] getRoles() {
                if (roles.length == 0) {
                    return new String[]{SUPERPROCESS_ROLE, SYSTEM_ROLE};
                } else {
                    return roles;
                }
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof Trusted && ((Trusted) o).getPrincipal().equals(principal);
            }
            @Override
            public int hashCode() {
                return Objects.hashCode(principal);
            }

        };
    }
}
