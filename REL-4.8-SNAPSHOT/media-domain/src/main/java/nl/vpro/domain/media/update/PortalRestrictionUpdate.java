/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.Date;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.PortalRestriction;
import nl.vpro.domain.user.Portal;

import static nl.vpro.domain.Xmlns.UPDATE_NAMESPACE;

/**
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    name = "portalRestrictionUpdateType",
    namespace = UPDATE_NAMESPACE)
public class PortalRestrictionUpdate {
    protected Date start;

    protected Date stop;

    protected String portal;

    public PortalRestrictionUpdate() {
    }

    public static PortalRestrictionUpdate of(String portal) {
        PortalRestrictionUpdate update = new PortalRestrictionUpdate();
        update.portal = portal;
        return update;
    }

    public PortalRestrictionUpdate(PortalRestriction restriction) {
        this.start = restriction.getStart();
        this.stop = restriction.getStop();
        this.portal = restriction.getPortal().getId();
    }

    public PortalRestriction toPortalRestriction() {
        return new PortalRestriction(new Portal(portal, portal), start, stop);
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
    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }
}
