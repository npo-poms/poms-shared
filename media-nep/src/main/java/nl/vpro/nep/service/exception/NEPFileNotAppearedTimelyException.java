package nl.vpro.nep.service.exception;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
public class NEPFileNotAppearedTimelyException extends NEPFileNotFoundException {
    public NEPFileNotAppearedTimelyException(Exception originalException, String errorMessage) {
        super(originalException, errorMessage);
    }
}
