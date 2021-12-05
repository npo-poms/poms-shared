package nl.vpro.domain.gtaa;

import lombok.Getter;

public class GTAAError extends RuntimeException {

    @Getter
    private final int statusCode;

    @Getter
    private String responseBodyAsString;

    public GTAAError(int statusCode, String responseBodyAsString, String s) {
        super(s);
        this.responseBodyAsString = responseBodyAsString;
        this.statusCode = statusCode;
    }
}
