package nl.vpro.sourcingservice.v2;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import nl.vpro.domain.media.Schedule;
import nl.vpro.sourcingservice.AbstractResponse;
import nl.vpro.util.DateUtils;

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

    public nl.vpro.sourcingservice.StatusResponse normalize() {
          return new nl.vpro.sourcingservice.StatusResponse(getStatus(),
              nl.vpro.sourcingservice.StatusResponse.Response.builder()
                  .mid(response.prid)
                  .original_filename(response.getOriginal_filename())
                  .filename(response.getFilename())
                  .created_at(response.getCreated_at())
                  .filesent_at(response.getFilesent_at())
                  .streamstatus_updated(response.getStreamstatus_updated())
                  .published_at(DateUtils.toInstant(response.getPublished_at(), Schedule.ZONE_ID))
                  .deleted_at(response.getDeleted_at())
                  .build()
            );
    }
}
