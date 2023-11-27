package nl.vpro.sourcingservice.v1;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.sourcingservice.AbstractResponse;

@Getter
@ToString()
@Deprecated
public class IngestResponse extends AbstractResponse {

    final Response response;

    @JsonCreator
    public IngestResponse(
        @JsonProperty("status") String status,
        @JsonProperty("response") Response response) {
        super(status);
        this.response = response;
    }

    @Data
    static class Response {
        private String media_id;
        private String original_filename;
        private String filename;
    }
}
