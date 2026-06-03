package nl.vpro.berlijn.domain.availability;


import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.berlijn.domain.AssertValidatable;
import nl.vpro.domain.media.MediaIdentifiable;
import nl.vpro.domain.media.MediaType;

@JsonIgnoreProperties({
    "type", //
    "metadata",// just contains things we're not interested in


    // come in via MediaIdentifiable
    "mid", "mediaType", "crids", "id"

})
@JsonSerialize
public record Availability(
    String version, // assert 1.0?
    Instant timestamp,
    AvailabilityContents contents
    ) implements MediaIdentifiable, AssertValidatable {

    @Override
    public void assertValid() {
        contents.assertValid();
    }

    @Override
    public String getMid() {
        return contents.prid();
    }

    @Override
    public MediaType getMediaType() {
        return null;
    }

    @Override
    public String toString() {
        return contents.prid() + " " + version + " " + timestamp;
    }
}
