package nl.npo.envelope;

import java.time.Instant;
import java.util.*;

public record NotifyEnvelope(String version, Instant timestamp, Map<String, Object> metadata, String type, String contents) {


    // UTF-8 BOM bytes
    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    public byte[] bytes()  {
        byte[] decoded = Base64.getDecoder().decode(contents);
        return stripBom(decoded);
    }

    public Optional<String> simpleFileName() {
        return Optional.ofNullable(metadata)
            .map(m -> m.get("fileName"))
            .map(Object::toString)
            .map(path -> path.substring(path.lastIndexOf('/') + 1));
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
}
