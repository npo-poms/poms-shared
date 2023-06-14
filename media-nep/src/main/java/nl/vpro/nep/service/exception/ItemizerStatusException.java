package nl.vpro.nep.service.exception;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

import nl.vpro.nep.domain.FailureResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
@Getter
@ToString
public class ItemizerStatusException extends NEPException {

    @Serial
    private static final long serialVersionUID = 3080658033118493320L;

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
