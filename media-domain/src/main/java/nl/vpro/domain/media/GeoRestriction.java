package nl.vpro.domain.media;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.time.Instant;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoRestrictionType")
@SuppressWarnings("serial")
public class GeoRestriction extends Restriction {

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "nl.vpro.constraints.NotNull")
    protected Region region;

    @XmlTransient
    private boolean authorityUpdate = false;

    public GeoRestriction() {
    }

    public GeoRestriction(String region) {
        // When loading from JSON
        String[] split = region.split(":", 2);

        if (split.length == 1) {
            this.region = Region.valueOf(split[0]);
        } else {
            this.region = Region.valueOf(split[1]);
        }
    }

    public GeoRestriction(Region region) {
        this.region = region;
    }

    public GeoRestriction(Region region, Instant start, Instant stop) {
        super(start, stop);
        this.region = region;
        authorityUpdate = true;
    }

    public GeoRestriction(Long id, Region region, Instant start, Instant stop) {
        super(id, start, stop);
        this.region = region;
        authorityUpdate = true;
    }

    public GeoRestriction(GeoRestriction source) {
        super(source);
        this.region = source.region;
        this.authorityUpdate = source.authorityUpdate;
    }

    public static GeoRestriction copy(GeoRestriction source){
        if(source == null) {
            return null;
        }
        return new GeoRestriction(source);
    }


    @XmlAttribute(name = "regionId")
    @JsonValue
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        if (region != this.region) {
            authorityUpdate = true;
        }
        this.region = region;
    }

    public boolean isAuthorityUpdate() {
        return authorityUpdate;
    }

    public void setAuthorityUpdate(boolean ceresUpdate) {
        this.authorityUpdate = ceresUpdate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GeoRestriction rhs = (GeoRestriction) obj;
        return new EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(region, rhs.region)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 53)
            .appendSuper(super.hashCode())
            .append(region)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("region", region)
            .toString();
    }
}
