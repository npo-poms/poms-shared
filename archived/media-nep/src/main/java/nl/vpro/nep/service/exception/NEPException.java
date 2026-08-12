package nl.vpro.nep.service.exception;

import lombok.Getter;

import java.io.Serial;

public class NEPException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5638799312282876320L;
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
