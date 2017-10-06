package nl.vpro.domain.api;

import java.time.Instant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlType(name = "dateFacetResultItemType")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class DateFacetResultItem extends RangeFacetResultItem<Instant> {

    public DateFacetResultItem() {
    }

    @lombok.Builder
    public DateFacetResultItem(String name, Instant begin, Instant end, long count) {
        super(name, begin, end, count);
    }

    @Override
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Instant getBegin() {
        return begin;
    }

    public void setBegin(Instant date) {
        this.begin = date;
    }

    @Override
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Instant getEnd() {
        return end;
    }

    public void setEnd(Instant date) {
        this.end = date;
    }

}

