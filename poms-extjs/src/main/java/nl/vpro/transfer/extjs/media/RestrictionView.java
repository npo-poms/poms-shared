package nl.vpro.transfer.extjs.media;

import nl.vpro.domain.media.GeoRestriction;
import nl.vpro.domain.media.PortalRestriction;
import nl.vpro.domain.media.Region;
import nl.vpro.domain.media.Restriction;
import nl.vpro.domain.user.Portal;
import nl.vpro.transfer.extjs.ExtRecord;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.time.Instant;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "portalId",
        "portal",
        "start",
        "stop",
        "region"
        })
public class RestrictionView extends ExtRecord {

    private Long id;

    private String portalId;

    private String portal;

    private String region;

    private Date start;

    private Date stop;

    private RestrictionView() {
    }

    private RestrictionView(Long id, String portalId, String portal, Date start, Date stop) {
        this.id = id;
        this.portalId = portalId;
        this.portal = portal;
        this.start = start;
        this.stop = stop;
    }

    private RestrictionView(Long id, String region, Date start, Date stop) {
        this.id = id;
        this.region = region;
        this.start = start;
        this.stop = stop;
    }

    private RestrictionView(Long id, String portalId, String portal, Instant start, Instant stop) {
        this.id = id;
        this.portalId = portalId;
        this.portal = portal;
        this.start = Date.from(start);
        this.stop = Date.from(stop);
    }

    private RestrictionView(Long id, String region, Instant start, Instant stop) {
        this.id = id;
        this.region = region;
        this.start = Date.from(start);
        this.stop = Date.from(stop);
    }

    public static <T extends Restriction> RestrictionView create(T restriction) {
        if(restriction instanceof PortalRestriction) {
            return createPortalRestriction((PortalRestriction)restriction);
        } else if(restriction instanceof GeoRestriction) {
            return createGeoRestriction((GeoRestriction)restriction);
        } else {
            throw new UnsupportedOperationException("No support for " + restriction.getClass().getSimpleName());
        }
    }

    private static RestrictionView createPortalRestriction(PortalRestriction restriction) {
        return new RestrictionView(
            restriction.getId(),
            restriction.getPortal().getId(),
            restriction.getPortal().getDisplayName(),
            restriction.getStart(),
            restriction.getStop()
        );
    }

    private static RestrictionView createGeoRestriction(GeoRestriction restriction) {
        return new RestrictionView(
            restriction.getId(),
            restriction.getRegion().name(),
            restriction.getStart(),
            restriction.getStop()
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends Restriction> T toRestriction(Class<T> clazz) {
        if(clazz.equals(PortalRestriction.class)) {
            return (T) new PortalRestriction(this.id, new Portal(portalId, portal), start.toInstant(), stop.toInstant());
        } else if(clazz.equals(GeoRestriction.class)) {
            return (T) new GeoRestriction(this.id, Region.valueOf(region), start.toInstant(), stop.toInstant());
        }

        throw new UnsupportedOperationException("No support for " + clazz.getSimpleName());
    }

    public <T extends Restriction> void updateTo(T restriction) {
        if(restriction instanceof PortalRestriction) {
            ((PortalRestriction)restriction).setPortal(new Portal(portalId, portal));
        } else if(restriction instanceof GeoRestriction) {
            ((GeoRestriction)restriction).setRegion(Region.valueOf(region));
        } else {
            throw new UnsupportedOperationException("No support for " + restriction.getClass().getSimpleName());
        }

        restriction.setStart(this.start.toInstant());
        restriction.setStop(this.stop.toInstant());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPortalId() {
        return portalId;
    }

    public void setPortalId(String portalId) {
        this.portalId = portalId;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
    public String toString() {
        return "RestrictionView [id=" + id + ", portal=" + portal
            + ", publishStart=" + start + ", publishStop="
            + stop + ", restriction=" + region + "]";
    }

}
