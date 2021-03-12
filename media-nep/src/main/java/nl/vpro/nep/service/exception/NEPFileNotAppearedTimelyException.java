package nl.vpro.nep.service.exception;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class NEPFileNotAppearedTimelyException extends NEPException {
    public NEPFileNotAppearedTimelyException(Exception originalException, String errorMessage) {
        super(originalException, errorMessage);
    }
}
