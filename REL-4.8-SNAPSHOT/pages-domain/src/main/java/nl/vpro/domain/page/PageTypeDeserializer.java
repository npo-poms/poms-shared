package nl.vpro.domain.page;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class PageTypeDeserializer extends JsonDeserializer<PageType> {
    @Override
    public PageType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = jp.getText();
        if ("NEWS".equals(value)) {
            value = "ARTICLE";
        }
        return PageType.valueOf(value);

    }
}
