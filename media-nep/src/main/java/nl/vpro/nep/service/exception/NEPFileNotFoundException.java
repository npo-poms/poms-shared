package nl.vpro.nep.service.exception;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
public class NEPFileNotFoundException extends NEPException {
    public NEPFileNotFoundException(Exception originalException, String errorMessage) {
        super(originalException, errorMessage);
    }
}
