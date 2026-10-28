package nl.npo.envelope;

import lombok.SneakyThrows;

import java.time.Instant;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * Kafka value envelop, where the content is more json
 * @param version
 * @param timestamp
 * @param metadata
 * @param type
 * @param contents
 */
public record JsonNodeEnvelope(String version, Instant timestamp, Map<String, Object> metadata, String type, JsonNode contents) {

    final static Jackson2Mapper mapper = Jackson2Mapper.getInstance();

    @SneakyThrows
    public byte[] bytes()  {
        return mapper.writeValueAsBytes(contents);
    }
}
