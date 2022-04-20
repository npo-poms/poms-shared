package nl.vpro.domain.media;

import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonValue;

import nl.vpro.domain.user.Portal;

@Entity
@Table
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "portalRestrictionType")
public class PortalRestriction extends Restriction<PortalRestriction> {

    private static final long serialVersionUID = -4235262587015174878L;

    public static class Builder extends RestrictionBuilder<Builder> {

    }
    @ManyToOne(optional = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NotNull(message = "nl.vpro.constraints.NotNull")
    protected Portal portal;

    public PortalRestriction() {
    }

    public PortalRestriction(Portal portal) {
        this.portal = portal;
    }

    public PortalRestriction(Portal portal, Instant start, Instant stop) {
        super(start, stop);
        this.portal = portal;
    }

    @lombok.Builder(builderClassName = "Builder")
    public PortalRestriction(
        Long id,
        Portal portal,
        Instant start,
        Instant stop) {
        super(id, start, stop);
        this.portal = portal;
    }

    public PortalRestriction(PortalRestriction source) {
        super(source);
        this.portal = source.portal;
    }

    /**
     * Added to make it possible to unmarshall a PortalRestriction from its JSON value, which is just the portal ID...
     */
    public PortalRestriction(String portalId) {
        this(new Portal(portalId, null));
    }

    public static PortalRestriction copy(PortalRestriction source) {
        if(source == null) {
            return null;
        }
        return new PortalRestriction(source);
    }

    public Portal getPortal() {
        return portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    @XmlAttribute(name = "portalId")
    @JsonValue
    public String getPortalId() {
        return portal.getId();
    }

    public void setPortalId(String portalId) {
        portal = new Portal(portalId, portalId);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(obj.getClass() != getClass()) {
            return false;
        }
        PortalRestriction rhs = (PortalRestriction)obj;
        return new EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(portal, rhs.portal)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 41)
            .appendSuper(super.hashCode())
            .append(portal)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("portal", portal)
            .toString();
    }
}
