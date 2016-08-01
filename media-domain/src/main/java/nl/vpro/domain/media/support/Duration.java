/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.bind.DurationToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.persistence.DurationToTimeConverter;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.FalseToNullAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 */
@Embeddable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "durationType",
    namespace = Xmlns.SHARED_NAMESPACE,
    propOrder = {
    "duration"
})
@JsonSerialize(using = DurationToJsonTimestamp.Serializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
public class Duration implements Serializable {

    private static long serialVersionUID = 0L;

    @Column(name = "duration_authorized")
    @XmlAttribute
    @XmlJavaTypeAdapter(value = FalseToNullAdapter.class)
    private Boolean authorized = false;

    @Column(name = "duration_value")
    @XmlValue
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.Deserializer.class)
    @Convert(converter = DurationToTimeConverter.class)
    @NotNull
    private java.time.Duration duration;

    public static Duration of(long amount, ChronoUnit unit) {
        return new Duration(java.time.Duration.of(amount, unit));
    }

    public static Duration ofMillis(long amount) {
        return new Duration(java.time.Duration.ofMillis(amount));
    }

    public static Duration of(java.time.Duration duration) {
        return duration == null ? null : new Duration(duration);
    }

    public Duration() {
    }

    public Duration(Date value) {
        this.duration = value == null ? null : java.time.Duration.of(value.toInstant().toEpochMilli(), ChronoUnit.MILLIS);
    }

    public Duration(java.time.Duration value) {
        this.duration = value;
    }

    public Duration(Date value, boolean authorized) {
        this(value);
        this.authorized = authorized;
    }

    public Duration(java.time.Duration value, boolean authorized) {
        this.duration = value;
        this.authorized = authorized;
    }


    public Duration(Duration source) {
        this(source.duration, source.authorized);
    }

    public static Duration copy(Duration source) {
        if(source == null) {
            return null;
        }
        return new Duration(source);
    }

    public boolean isAuthorized() {
        return authorized != null ? authorized : false;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public Date getValue() {
        return duration == null ? null : new Date(duration.toMillis());
    }

    public void setValue(Date value) {
        this.duration = value == null ? null : java.time.Duration.of(value.getTime(), ChronoUnit.MILLIS);
    }

    /**
     * Returns the Duration as a java.time.Duration object.
     * @since 4.3
     */
    public void set(java.time.Duration duration) {
        this.duration = duration;
    }
    public java.time.Duration get() {
        return this.duration;
    }


    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Duration other = (Duration)o;

        if(authorized != null ? !authorized.equals(other.authorized) : other.authorized != null) {
            return false;
        }
        if(duration != null ? !duration.equals(other.duration) : other.duration!= null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = authorized != null ? authorized.hashCode() : 0;
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.duration + (authorized ? " (authorized)" : " (not authorized)");
    }
}
