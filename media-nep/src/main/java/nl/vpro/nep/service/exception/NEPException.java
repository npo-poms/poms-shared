package nl.vpro.nep.service.exception;

import lombok.Getter;

public class NEPException extends RuntimeException {

    @Getter
    private final Exception originalException;

    public NEPException(Exception originalException, String errorMessage) {
        super(errorMessage);
        this.originalException = originalException;
    }

    public NEPException(String errorMessage) {
        super(errorMessage);
        this.originalException = null;
    }

}
