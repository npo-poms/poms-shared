package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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


    @Getter
    @Setter
    private String name;

    public DateFacetResultItem() {
    }


    @lombok.Builder
    private DateFacetResultItem(String value, String name, Instant begin, Instant end, long count) {
        super(value, begin, end, count);
        this.name = name;
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

