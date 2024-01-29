package nl.vpro.api.rs.v3;

import java.util.Map;

import jakarta.ws.rs.core.MediaType;

import com.google.common.collect.ImmutableMap;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class MediaTypes {

    private MediaTypes() {
    }

    private static final Map<String, String> PARAMS = ImmutableMap.of("charset", "utf-8");
    public static final MediaType JSON = new MediaType("application", "json", PARAMS);

    public static final MediaType XML = new MediaType("application", "xml", PARAMS);


}
