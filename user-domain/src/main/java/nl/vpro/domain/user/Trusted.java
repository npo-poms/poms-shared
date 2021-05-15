package nl.vpro.domain.user;

import java.time.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.Roles;

import static nl.vpro.domain.Roles.SUPERPROCESS_ROLE;
import static nl.vpro.domain.Roles.SYSTEM_ROLE;

/**
 * Representation of a 'trusted' user. Such a user needs not to exist in signon system (i.e. keycloak).
 *
 * Instances of this are only done explicitly in java.
 *
 * @author Michiel Meeuwissen
 * @since 5.7
 */
public interface Trusted {

    String getPrincipal();

    String[] getRoles();

    /**
     * The last login valid for this user. Defaults to {@link Instant#now()}, but a {@link #copy()} will make this undynamic
     */
    Instant getLastLogin();

    /**
     * @param login A supplier for the instant to be returned by {@link #getLastLogin()}.
     *              This may e.g. be {@code Instant::now}, or a fixed value like {@code () -> instant}
     * @param principal The value for {@link #getPrincipal()}
     * @param roles The roles to associate with this user. If <em>no</em>  roles are given, this defaults to {@link Roles#SUPERPROCESS_ROLE}, {@link Roles#SYSTEM_ROLE}
     */
    static Trusted of(@NonNull Supplier<Instant> login, @NonNull String principal, @NonNull String @NonNull... roles) {

        return new Trusted() {
            private final String[] actualRoles;
            {
                if (roles.length == 0) {
                    this.actualRoles = new String[]{SUPERPROCESS_ROLE, SYSTEM_ROLE};
                } else {
                    this.actualRoles = roles;
                }
            }
            @Override
            public String getPrincipal() {
                return principal;
            }

            @Override
            public String[] getRoles() {
                return actualRoles;
            }

            @Override
            public Instant getLastLogin() {
                return login.get();
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof Trusted && ((Trusted) o).getPrincipal().equals(principal);
            }
            @Override
            public int hashCode() {
                return Objects.hashCode(principal);
            }

            @Override
            public String toString() {
                return "Trusted:" + principal + ":" + Arrays.asList(getRoles());
            }
        };
    }

    static Trusted of(String principal, String... roles) {
        return of(Clock.tick(Clock.systemUTC(), Duration.ofMinutes(5)), principal, roles);
    }

    static Trusted of(final Clock clock, final String principal, String... roles) {
        return of(clock::instant, principal, roles);
    }

    default Trusted of(Instant login) {
        return of(() -> login, getPrincipal(), getRoles());
    }

    /**
     * Returns a copy of this trusted user. The value for {@link #getLastLogin()} will however be frozen if it wasn't.
     */
    default Trusted copy() {
        return of(getLastLogin());
    }




}
