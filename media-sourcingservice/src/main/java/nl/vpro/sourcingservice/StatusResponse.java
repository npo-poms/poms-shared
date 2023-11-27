package nl.vpro.sourcingservice;

import lombok.*;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@ToString(callSuper = true)
public class StatusResponse extends AbstractResponse {


    final Response response;

    @JsonCreator
    public StatusResponse(@JsonProperty("status") String status, @JsonProperty("response") Response response) {
        super(status);
        this.response = response;
    }

    @Data
    @Builder
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
        String mid;
    }

}
