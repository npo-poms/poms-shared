package nl.vpro.domain.media;

import java.util.Date;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.media.support.DomainObject;

@MappedSuperclass
@nl.vpro.validation.Restriction
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.NONE)
@SuppressWarnings("serial")
abstract public class Restriction implements DomainObject {

    @Id
    @SequenceGenerator(name = "hibernate_sequences", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequences")
    @XmlTransient // Don't remove!
    private Long id;

    @Column
    @XmlAttribute
    protected Date start;

    @Column
    @XmlAttribute
    protected Date stop;

    protected Restriction() {
    }

    protected Restriction(Date start, Date stop) {
        this.start = start;
        this.stop = stop;
    }

    protected Restriction(Long id, Date start, Date stop) {
        this.id = id;
        this.start = start;
        this.stop = stop;
    }

    protected Restriction(Restriction source) {
        this(source.start, source.stop);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Restriction setId(Long id) {
        this.id = id;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Restriction rhs = (Restriction) obj;

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
}
