package nl.npo.envelope;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.jackson2.Jackson2Mapper;

public record NotifyEnvelope(String version, Instant timestamp, Map<String, Object> metadata, String type, String contents) {

    static final Jackson2Mapper mapper = Jackson2Mapper.getLenientInstance();

    // UTF-8 BOM bytes
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public byte[] bytes()  {
        byte[] decoded = Base64.getDecoder().decode(contents);
        return stripBom(decoded);
    }

    private static byte[] stripBom(byte[] bytes) {
        if (bytes.length >= 3 &&
            bytes[0] == UTF8_BOM[0] &&
            bytes[1] == UTF8_BOM[1] &&
            bytes[2] == UTF8_BOM[2]) {
            byte[] result = new byte[bytes.length - 3];
            System.arraycopy(bytes, 3, result, 0, result.length);
            return result;
        }
        return bytes;
    }
    public JsonNode json() throws IOException {
        return mapper.readTree(bytes());
    }

    public <T> List<T> unwrapJsonArray(ObjectMapper mapper, Class<T> clazz) throws IOException {
        return mapper.readerFor(mapper.getTypeFactory().constructCollectionType(List.class, clazz)).readValue(bytes());
    }

}
