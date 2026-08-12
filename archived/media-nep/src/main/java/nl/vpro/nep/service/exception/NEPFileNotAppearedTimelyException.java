package nl.vpro.nep.service.exception;

import java.io.Serial;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
public class NEPFileNotAppearedTimelyException extends NEPFileNotFoundException {
    @Serial
    private static final long serialVersionUID = -8317449015130169290L;

    public NEPFileNotAppearedTimelyException(Exception originalException, String errorMessage) {
        super(originalException, errorMessage);
    }
}
