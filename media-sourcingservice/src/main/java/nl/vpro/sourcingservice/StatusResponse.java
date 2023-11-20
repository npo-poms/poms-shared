package nl.vpro.sourcingservice;

import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Getter
@ToString()
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
        private String prid;
        private String current_status;
        private String original_filename;
        private String stored_filename;
        private String filename;
        private String asset_status;
        private String status;

        // silly that 1. time can't be parsed by default parser
        // 2. time seems to be in UTC, but doesn't indicate.
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime created_at;
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime filesent_at;
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime streamstatus_updated;
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime published_at;
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime to_be_deleted_at;
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime deleted_at;
        @JsonDeserialize(using = nl.vpro.jackson2.LocalDateTimeToJsonDateWithSpace.Deserializer.class)
        private LocalDateTime hard_delete_at;

        private JsonNode latest_notify_event;

    }
}
