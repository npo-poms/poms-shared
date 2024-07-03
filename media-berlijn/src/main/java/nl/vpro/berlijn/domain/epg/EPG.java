package nl.vpro.berlijn.domain.epg;

import lombok.extern.log4j.Log4j2;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.berlijn.domain.AssertValidatable;

@JsonIgnoreProperties({
})
@Log4j2
public record EPG(
    String type,
    String version, // assert 1.0?
    Instant timestamp,
    EPGContents contents,
    JsonNode metadata) implements AssertValidatable {


    @Override
    public String toString() {
        return contents().channelId() + ":" + contents().date() + ":" +  timestamp + "   " + contents().entries().size() + " programs";
    }


    @Override
    public void assertValid() {
        assert type.equals("notify");
        assert version.equals("1.0");

        contents().assertValid();
    }
}
