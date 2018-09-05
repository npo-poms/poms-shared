package nl.vpro.services;

import org.junit.Test;

import nl.vpro.domain.user.Trusted;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class MockDoAsTransactionServiceTest {

    MockDoAsTransactionService impl = new MockDoAsTransactionService();

    @Test(expected = Exception.class)
    public void executeInNewTransaction() {
        impl.executeInNewTransaction(Trusted.of("bla"), () -> {
            if (true) {
                throw new Exception("ex");
            }
            return "";

        });

    }
}
