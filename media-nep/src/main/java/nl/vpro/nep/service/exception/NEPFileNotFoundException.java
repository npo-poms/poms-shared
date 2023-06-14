package nl.vpro.nep.service.exception;

import java.io.Serial;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
public class NEPFileNotFoundException extends NEPException {
    @Serial
    private static final long serialVersionUID = 4236530524449302717L;

    public NEPFileNotFoundException(Exception originalException, String errorMessage) {
        super(originalException, errorMessage);
    }
}
