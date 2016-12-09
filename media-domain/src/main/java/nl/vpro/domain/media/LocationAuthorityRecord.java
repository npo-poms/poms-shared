/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.Identifiable;

/**
 * This used to be 'CeresRecord'. Then Ceres was renamed to Pluto.
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@Entity
public class LocationAuthorityRecord implements Identifiable<Long>, Comparable<LocationAuthorityRecord>, Serializable {

    private static final long serialVersionUID = 0L;

    private static final Logger LOG = LoggerFactory.getLogger(LocationAuthorityRecord.class);


    @Id
    @GeneratedValue
    protected Long id;

    @ManyToOne
    //@JoinColumn(name = "mediaobject_id", insertable = false, updatable = false)
    protected MediaObject mediaObject;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    protected Platform platform;


    private Boolean authority = false;

    @Column
    @XmlTransient
    private Instant restrictionStart;

    @Column
    @XmlTransient
    private Instant restrictionStop;

    public LocationAuthorityRecord() {
    }

    LocationAuthorityRecord(MediaObject object, Platform platform, Boolean authority) {
        this.authority = authority;
        this.platform = platform;
        this.mediaObject = object;
    }

    /**
     * Create an authoritative Record meaning that POMS users are allowed to manage restrictions via POMS.
     * Restriction changes propagate to Ceres/Pluto using the given GUCI.
     *
     * @return return the record
     */
    public static LocationAuthorityRecord authoritative(MediaObject object, Platform platform) {
        LocationAuthorityRecord record = new LocationAuthorityRecord(object, platform, true);
        if (object != null) {
            object.setLocationAuthorityRecord(record);
        }
        return record;
    }

    /**
     * Create an non-authoritative Record meaning that POMS users must manage media restrictions via the Ceres/Pluto
     * interfaces
     *
     * @return the record
     */
    public static LocationAuthorityRecord nonAuthoritative(MediaObject object, Platform platform) {
        LocationAuthorityRecord record = new LocationAuthorityRecord(object, platform, false);
        object.setLocationAuthorityRecord(record);
        return record;
    }

    public static LocationAuthorityRecord unknownAuthority(MediaObject object, Platform platform) {
        LocationAuthorityRecord record = new LocationAuthorityRecord(object, platform, null);
        object.setLocationAuthorityRecord(record);
        return record;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    /**
     * Determines if users have the authority to update Location on state change
     *
     * @return true when a user can update Ceres on restriction change, false otherwise
     */
    public boolean hasAuthority() {
        return authority != null && authority;
    }

    /**
     * Holds the Ceres/Pluto time restriction. Ceres/Pluto distributes restrictions even when no locations are available yet or
     * when they have been revoked previously.
     *
     * @return restrictionStart
     */
    public Instant getRestrictionStart() {
        return restrictionStart;
    }

    /**
     * See {@link #getRestrictionStart()}
     */
    public void setRestrictionStart(Instant restrictionStart) {
        if(restrictionStart != null && restrictionStop != null && restrictionStop.isBefore(restrictionStart)) {
            this.restrictionStart = restrictionStop;
        }
        if (restrictionStart == null && this.restrictionStart != null) {
            LOG.warn("Clearing restriction start of {}", this);
        }
        this.restrictionStart = restrictionStart;
    }

    /**
     * See {@link #getRestrictionStart()}
     */
    public Instant getRestrictionStop() {
        return restrictionStop;
    }

    /**
     * See {@link #getRestrictionStart()}
     */
    public void setRestrictionStop(Instant restrictionStop) {
        if(restrictionStop != null && restrictionStart != null && restrictionStop.isBefore(restrictionStart)) {
            this.restrictionStart = restrictionStop;
        }
        if (restrictionStop== null && this.restrictionStop != null) {
            LOG.warn("Clearing restriction stop of {}", this);
        }
        this.restrictionStop = restrictionStop;
    }

    @Override
    public Long getId() {
        return id;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mo) {
        this.mediaObject = mo;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LocationAuthorityRecord");
        sb.append("{id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        LocationAuthorityRecord that = (LocationAuthorityRecord)o;

        if(id != null && that.id != null) {
            return id.equals(that.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public int compareTo(LocationAuthorityRecord o) {
        if (platform == null) {
            return o == null ? 0 : o.platform == null ? 0 : 1;
        } else {
            return o == null || o.platform == null ? 1 : platform.compareTo(o.platform);
        }
    }
}
