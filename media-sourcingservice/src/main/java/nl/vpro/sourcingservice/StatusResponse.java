package nl.vpro.sourcingservice;

import lombok.*;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
public class StatusResponse {

    final String status;
    final Response response;

    @JsonCreator
    public StatusResponse(@JsonProperty("status") String status, @JsonProperty("response") Response response) {
        this.status = status;
        this.response = response;
    }

    @Data
    @lombok.Builder
    public static class Response {

        Instant created_at;
        Instant deleted_at;
        Instant hard_delete_at;
        Instant filesent_at;
        Instant streamstatus_updated;
        Instant published_at;
        String status;
        String filename;
        String original_filename;
        String prid;
    }

}
