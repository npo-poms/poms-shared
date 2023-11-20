package nl.vpro.sourcingservice;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@ToString()
public class UploadResponse extends AbstractResponse {

    final Response response;

    @JsonCreator
    public UploadResponse(
        @JsonProperty("status") String status,
        @JsonProperty("response") Response response) {
        super(status);
        this.response = response;
    }

    @Data
    public static class Response {
        private String media_id;
        private String original_filename;
        private String filename;
    }
}
