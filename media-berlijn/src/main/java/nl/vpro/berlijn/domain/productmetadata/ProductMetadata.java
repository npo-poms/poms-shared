package nl.vpro.berlijn.domain.productmetadata;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.berlijn.domain.AssertValidatable;
import nl.vpro.domain.media.MediaIdentifiable;
import nl.vpro.domain.media.MediaType;

@JsonIgnoreProperties({

    // come in via MediaIdentifiable
    "mid", "mediaType", "crids", "id"

})
public record ProductMetadata  (
    Type type,
    String version,
    Instant timestamp,
    JsonNode metadata, // ignorable?
    ProductMetadataContents contents

) implements MediaIdentifiable, AssertValidatable {


    @Override
    @JsonIgnore
    public String getMid() {
        return contents.prid();
    }


    /**
     * Just to satisfy {@link MediaIdentifiable}. This is something entirely different then {@link ProductMetadataContents#mediaType()}.
     * <p />
     * {@inheritDoc}
     */
    @Override
    public MediaType getMediaType() {
        if (contents.contentType() == null) {
            return null;
        }
        return switch (contents.contentType()) {
            case season  -> MediaType.SEASON;
            case series  -> MediaType.SERIES;
            case episode, programme -> MediaType.BROADCAST;

        };
    }

    @Override
    public String toString() {
        return (type == Type.delete ? "DELETE " : "") + getMid() + " " + contents.contentType() + " " + version + " " + timestamp;
    }


    public boolean looksLikeRadio() {
        return contents.mediaType() == nl.vpro.berlijn.domain.productmetadata.MediaType.audio ||
            contents.contentType() == ContentType.season && PridHolder.isEmpty(contents.relations().series()) // heuristic to recognized rcrs?
            ;
    }

    /**
     * Just perform soms java assertions
     */
    @Override
    public void assertValid() {
        if (contents.contentType() == ContentType.programme) {
            var relations = contents.relations();
            if (relations != null) {
                // wtf already, but any way
                assert relations.seasons() == null : "Incoming %s has seasons: %s".formatted(contents, relations.seasons());
                assert PridHolder.isEmpty(relations.series()) : "Incoming %s has series: %s".formatted(contents, relations.series());
                assert PridHolder.isEmpty(relations.season()) :  "Incoming %s has series: %s".formatted(contents, relations.season());
            }
        }
        if (contents.contentType() == ContentType.episode) {
            var relations = contents.relations();
            assert relations != null;
            assert relations.season() != null;
            assert relations.season().prid() != null;
            assert ! relations.season().prid().isBlank();


            if (relations.series() == null || relations.series().prid() == null || relations.series().prid().isBlank()) {
                LogManager.getLogger(ProductMetadata.class).warn("No series information for episode {}", this);
            }
        }

        if (contents.contentType() == ContentType.season) {
            var relations = contents.relations();
            assert relations != null;
            assert PridHolder.isEmpty(relations.season());

            assert relations.series() != null;
            assert relations.series().prid() != null;
            assert ! relations.series().prid().isBlank();
        }
        assert contents().prid() != null;

        assert contents().contentType() != null;

    }
}
