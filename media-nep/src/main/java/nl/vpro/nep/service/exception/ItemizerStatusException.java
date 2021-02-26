package nl.vpro.nep.service.exception;

import lombok.Getter;
import lombok.ToString;

import nl.vpro.nep.domain.FailureResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
@Getter
@ToString
public class ItemizerStatusException extends NEPException {

    private final FailureResponse response;


    private final int statusCode;

    public ItemizerStatusException(int statusCode, FailureResponse failure) {
        super(failure.getErrors());
        this.response = failure;
        this.statusCode = statusCode;
    }
    public ItemizerStatusException(int statusCode, String error) {
        super(error);
        this.response = null;
        this.statusCode = statusCode;
    }
}
