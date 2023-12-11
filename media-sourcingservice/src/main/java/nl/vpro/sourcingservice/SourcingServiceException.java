package nl.vpro.sourcingservice;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

import com.fasterxml.jackson.databind.JsonNode;


@ToString
@Getter
public class SourcingServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7473387371139498878L;

    final JsonNode body;

    final int statusCode;

    public SourcingServiceException(int statusCode, JsonNode body) {
        super(body.toString());
        this.body = body;
        this.statusCode = statusCode;
    }

    public SourcingServiceException(int statusCode, String body) {
        super(body);
        this.body = null;
        this.statusCode = statusCode;
    }
}
