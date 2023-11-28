package nl.vpro.sourcingservice.v1;

import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.domain.media.Schedule;
import nl.vpro.sourcingservice.AbstractResponse;
import nl.vpro.util.DateUtils;

@Getter
@ToString()
@Deprecated
public class StatusResponse  extends AbstractResponse {

    final Response response;

    @JsonCreator
    public StatusResponse(@JsonProperty("status") String status, @JsonProperty("response") Response response) {
        super(status);
        this.response = response;
    }

    @Data
    public static class Response {
        private String id;
        private String current_status;
        private String original_filename;
        private String stored_filename;
        private String filename;
        private String asset_status;
        // silly that 1. time can't be parsed by default parser
        // 2. time seems to be in UTC, but doesn't indicate.
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime created_at;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime filesent_at;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime streamstatus_updated;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime published_at;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime to_be_deleted_at;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime deleted_at;

        private JsonNode latest_notify_event;

    }

    public nl.vpro.sourcingservice.StatusResponse normalize() {
        return new nl.vpro.sourcingservice.StatusResponse(getStatus(),
            nl.vpro.sourcingservice.StatusResponse.Response.builder()
                .mid(response.getId())
                .original_filename(response.getOriginal_filename())
                .filename(response.getFilename())
                .created_at(DateUtils.toInstant(response.getCreated_at(), Schedule.ZONE_ID))
                .filesent_at(DateUtils.toInstant(response.getFilesent_at(), Schedule.ZONE_ID))
                .streamstatus_updated(DateUtils.toInstant(response.getStreamstatus_updated(), Schedule.ZONE_ID))
                .published_at(DateUtils.toInstant(response.getPublished_at(), Schedule.ZONE_ID))
                .deleted_at(DateUtils.toInstant(response.getDeleted_at(), Schedule.ZONE_ID))
                .build()
            );
    }
}
