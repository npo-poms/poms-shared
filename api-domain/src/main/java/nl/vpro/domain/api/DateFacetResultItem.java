package nl.vpro.domain.api;

import java.io.IOException;
import java.util.Date;

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
 * @since 2.0
 */
@XmlType(name = "dateFacetResultItemType")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DateFacetResultItem extends RangeFacetResultItem<Date> {

    public DateFacetResultItem() {
    }

    public DateFacetResultItem(String name, Date begin, Date end, long count) {
        super(name, begin, end, count);
    }

    @Override
    @JsonSerialize(using = DateFacetResultItem.DateSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date date) {
        this.begin = date;
    }

    @Override
    @JsonSerialize(using = DateFacetResultItem.DateSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date date) {
        this.end = date;
    }



    // Work around
    // https://jira.codehaus.org/browse/JACKSON-920
    public static class DateSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeNumber(value.getTime());

        }
    }



}

