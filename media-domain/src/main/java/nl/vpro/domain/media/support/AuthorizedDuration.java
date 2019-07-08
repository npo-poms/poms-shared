/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
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
    })
@JsonSerialize(using = DurationToJsonTimestamp.Serializer.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
public class AuthorizedDuration implements Serializable, TemporalAmount {

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
    //@Convert(converter = DurationToTimeCESTConverter.class)
    @NotNull
    private java.time.Duration duration;

    public static AuthorizedDuration of(long amount, ChronoUnit unit) {
        return of(java.time.Duration.of(amount, unit));
    }

    public static AuthorizedDuration ofMillis(long amount) {
        return of(java.time.Duration.ofMillis(amount));
    }

    public static AuthorizedDuration of(java.time.Duration duration) {
        if (duration == null) {
            return null;
        }
        return new AuthorizedDuration(duration);
    }


    public static Duration duration(AuthorizedDuration duration) {
        if (duration == null) {
            return null;
        }
        return duration.get();
    }

    /*public static Duration ofTemporalAmount(TemporalAmount duration) {
        if (duration == null) {
            return null;
        }
        java.time.Duration value  = java.time.Duration.ZERO;
        for (TemporalUnit unit : duration.getUnits()) {
            value = value.plus(java.time.Duration.of(duration.get(unit), unit));
        }
        return new Duration(value);
    }*/

    public static AuthorizedDuration authorized(java.time.Duration duration) {
        return duration == null ? null : new AuthorizedDuration(duration, true);
    }

    public AuthorizedDuration() {
    }

    @Deprecated
    public AuthorizedDuration(Date value) {
        this.duration = value == null ? null : java.time.Duration.of(value.toInstant().toEpochMilli(), ChronoUnit.MILLIS);
    }

    public AuthorizedDuration(java.time.Duration value) {
        this.duration = value;
    }


    @Deprecated
    public AuthorizedDuration(Date value, boolean authorized) {
        this(value);
        this.authorized = authorized;
    }

    public AuthorizedDuration(java.time.Duration value, boolean authorized) {
        this.duration = value;
        this.authorized = authorized;
    }


    public AuthorizedDuration(AuthorizedDuration source) {
        this(source.duration, source.authorized);
    }

    public static AuthorizedDuration copy(AuthorizedDuration source) {
        if(source == null) {
            return null;
        }
        return new AuthorizedDuration(source);
    }

    public boolean isAuthorized() {
        return authorized != null ? authorized : false;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    @Deprecated
    public Date getValue() {
        return asDate(this);
    }

    @Deprecated
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

        AuthorizedDuration other = (AuthorizedDuration)o;

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

    public static java.time.Duration get(AuthorizedDuration dur) {
        return dur == null ? null : dur.get();
    }

    @Deprecated
    public static java.util.Date asDate(AuthorizedDuration dur) {
        if (dur == null) {
            return null;
        }
        return dur.duration == null ? null : new Date(dur.duration.toMillis());
    }

    @Override
    public long get(TemporalUnit unit) {
        return get().get(unit);

    }

    @Override
    public List<TemporalUnit> getUnits() {
        return get().getUnits();
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return get().addTo(temporal);

    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return get().subtractFrom(temporal);
    }
}
