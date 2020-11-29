package nl.vpro.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.user.Trusted;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class MockDoAsTransactionServiceTest {

    MockDoAsTransactionService impl = new MockDoAsTransactionService();

    @Test
    public void executeInNewTransaction() {
        Assertions.assertThatThrownBy(() -> {
            impl.executeInNewTransaction(Trusted.of("bla"), () -> {
                if (true) {
                    throw new Exception("ex");
                }
                return "";

            });
        }).isInstanceOf(Exception.class);

    }
}
