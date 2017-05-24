package nl.vpro.domain.api;

import java.io.IOException;
import java.time.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@XmlType(name = "durationFacetResultItemType")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DurationFacetResultItem extends RangeFacetResultItem<Duration> {

    public DurationFacetResultItem() {
    }

    public DurationFacetResultItem(String name, Duration begin, Duration end, long count) {
        super(name, begin, end, count);
    }

    @Override
    @JsonSerialize(using = DurationFacetResultItem.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Duration getBegin() {
        return begin;
    }

    public void setBegin(Duration date) {
        this.begin = date;
    }

    @Override
    @JsonSerialize(using = DurationFacetResultItem.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Duration getEnd() {
        return end;
    }

    public void setEnd(Duration date) {
        this.end = date;
    }



    // Work around
    // https://jira.codehaus.org/browse/JACKSON-920
    public static class Serializer extends JsonSerializer<Duration> {
        @Override
        public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeNumber(value.toMillis());

        }
    }



}

