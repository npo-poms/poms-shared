package nl.vpro.domain.media;

import java.io.Serial;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.validation.ValidEmbargo;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

@MappedSuperclass
@ValidEmbargo
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.NONE)
abstract public class Restriction<T extends Restriction<T>> extends DomainObject implements MutableEmbargo<T> { // Child<MediaObject>

    @Serial
    private static final long serialVersionUID = 4916863601110482209L;

    protected static abstract class RestrictionBuilder<B extends RestrictionBuilder<B>> implements EmbargoBuilder<B> {


        public abstract B start(Instant date);

        public abstract B stop(Instant date);

        @Override
        public B publishStop(Instant date) {
            return stop(date);
        }

        @Override
        public B publishStart(Instant date) {
            return start(date);
        }


    }

    @Column
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    protected Instant start;

    @Column
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    protected Instant stop;

    protected Restriction() {
    }

    protected Restriction(Instant start, Instant stop) {
        this.start = start;
        this.stop = stop;
    }

    protected Restriction(Long id, Instant start, Instant stop) {
        this.id = id;
        this.start = start;
        this.stop = stop;
    }

    protected Restriction(Restriction<T> source) {
        this(source.start, source.stop);
    }

    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public Instant getStop() {
        return stop;
    }

    public void setStop(Instant stop) {
        this.stop = stop;
    }



    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Restriction<T> rhs = (Restriction) obj;

        return new EqualsBuilder()
            .append(start, rhs.start)
            .append(stop, rhs.stop)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 53)
            .append(start)
            .append(stop)
            .toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);

        if(id != null) {
            builder.appendSuper(super.toString());
        }

        return builder
            .append("start", start)
            .append("stop", stop)
            .toString();
    }

    @Override
    public Instant getPublishStartInstant() {
        return getStart();

    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public T setPublishStartInstant(Instant publishStart) {
        setStart(publishStart);
        return (T) this;

    }

    @Override
    public Instant getPublishStopInstant() {
        return getStop();

    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public T setPublishStopInstant(Instant publishStop) {
        setStop(publishStop);
        return (T) this;

    }
}
