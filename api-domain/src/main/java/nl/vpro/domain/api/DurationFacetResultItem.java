package nl.vpro.domain.api;

import java.time.Duration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DefaultDurationXmlAdapter;

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
    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Duration getBegin() {
        return begin;
    }

    public void setBegin(Duration date) {
        this.begin = date;
    }

    @Override
    @XmlJavaTypeAdapter(DefaultDurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Duration getEnd() {
        return end;
    }

    public void setEnd(Duration date) {
        this.end = date;
    }





}

