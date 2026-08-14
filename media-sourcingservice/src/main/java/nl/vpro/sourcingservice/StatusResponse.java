package nl.vpro.sourcingservice;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.update.TranscodeStatus;

@Getter
@Slf4j
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
    @AllArgsConstructor
    @NoArgsConstructor
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

        public TranscodeStatus.Status transcodeStatus() {
            if (status == null) {
                return null;
            }
            switch (status.toLowerCase()) {
                case "pending":
                    return TranscodeStatus.Status.RUNNING;
                default:
                    log.warn("Unknown srcs status: " + status);
                    return null;
            }
        }
    }

}
