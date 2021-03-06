package nl.vpro.domain.user;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.*;

import javax.security.enterprise.CallerPrincipal;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static nl.vpro.mdc.MDCConstants.USER_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
class UserServiceTest {

    private UserService<User> userService = new UserService<User>() {
        @Override
        public boolean needsUpdate(User oldUser, User newUser) {
            return false;

        }

        @Override
        public <S> S doAs(String principalId, Callable<S> handler) throws Exception {
            return null;

        }

        @Override
        public <S> S systemDoAs(String principalId, Callable<S> handler) throws Exception {
            return null;

        }

        @Override
        public User get(Principal authentication) {
            return null;

        }

        @Override
        public Optional<User> get(@NonNull String id) {
            return Optional.empty();

        }

        @Override
        public List<? extends User> findUsers(String name, int limit) {
            return null;

        }

        @Override
        public User update(User user) {
            return null;

        }

        @Override
        public void delete(User object) {
            log.info("--");

        }

        @Override
        public Optional<User> currentUser() {
            return Optional.empty();

        }

        @Override
        public User authenticate(String principalId, String password) {
            return null;

        }

        @Override
        public Optional<User> authenticate(String principalId) {
            return Optional.empty();

        }

        @Override
        public boolean currentUserHasRole(Collection<String> roles) {
            return false;

        }

        @Override
        public Principal getAuthentication() {
            return new CallerPrincipal("foobar");

        }

        @Override
        public void restoreAuthentication(Principal authentication) {
            log.info("--");

        }

        @Override
        public void dropAuthentication() {
            log.info("--");

        }
    };

    @Test
    public void async() throws ExecutionException, InterruptedException {
        MDC.put(USER_NAME, "user");
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            int fi = i;
            futures.add(userService.async(() -> {
                MDC.put(USER_NAME, "bloe " + fi);
                Thread.sleep(100);
                return "hoi";
            }, log));

        }
        for (CompletableFuture<?> f : futures) {
            f.get();
        }
        assertThat(UserService.ASYNC_EXECUTOR.getPoolSize()).isGreaterThan(1);
        log.info("after "  + UserService.ASYNC_EXECUTOR.getPoolSize());

    }

}
