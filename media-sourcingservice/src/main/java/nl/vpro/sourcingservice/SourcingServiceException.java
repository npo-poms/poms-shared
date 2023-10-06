package nl.vpro.sourcingservice;

import lombok.Getter;

import java.io.Serial;

import com.fasterxml.jackson.databind.JsonNode;


public class SourcingServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7473387371139498878L;

    @Getter
    final JsonNode body;

    @Getter
    final int statusCode;

    public SourcingServiceException(int statusCode, JsonNode body) {
        super(body.toString());
        this.body = body;
        this.statusCode = statusCode;
    }
}
