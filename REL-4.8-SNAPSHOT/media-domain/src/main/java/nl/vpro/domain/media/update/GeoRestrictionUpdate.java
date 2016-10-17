/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.Date;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.Region;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    name = "geoRestrictionUpdateType"
)
public class GeoRestrictionUpdate {
    protected Date start;

    protected Date stop;

    protected Region region;

    public GeoRestrictionUpdate() {
    }

    public GeoRestrictionUpdate(GeoRestriction restriction) {
        this.start = restriction.getStart();
        this.stop = restriction.getStop();
        this.region = restriction.getRegion();
    }

    public GeoRestriction toGeoRestriction() {
        return new GeoRestriction(region, start, stop);
    }

    @XmlAttribute
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    @XmlAttribute
    public Date getStop() {
        return stop;
    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    @XmlValue
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
