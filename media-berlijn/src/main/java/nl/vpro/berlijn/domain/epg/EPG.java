package nl.vpro.berlijn.domain.epg;

import lombok.extern.log4j.Log4j2;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import nl.vpro.berlijn.domain.AssertValidatable;

@JsonIgnoreProperties({
    "type", // always 'notify' ?
    "metadata" // just contains things we're not interested in
})
@Log4j2
public record EPG(
    String version, // assert 1.0?
    Instant timestamp,
    EPGContents contents) implements AssertValidatable {


    @Override
    public String toString() {
        return contents().channelId() + ":" + contents().date() + ":" +  timestamp + "   " + contents().entries().size() + " programs";
    }


    @Override
    public void assertValid() {
        contents().assertValid();
    }
}
