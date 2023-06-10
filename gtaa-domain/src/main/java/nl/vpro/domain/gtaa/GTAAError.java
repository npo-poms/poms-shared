package nl.vpro.domain.gtaa;

import lombok.Getter;

import java.io.Serial;

public class GTAAError extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8827634631411664540L;

    @Getter
    private final int statusCode;

    @Getter
    private final String responseBodyAsString;

    public GTAAError(int statusCode, String responseBodyAsString, String s) {
        super(s);
        this.responseBodyAsString = responseBodyAsString;
        this.statusCode = statusCode;
    }
}
