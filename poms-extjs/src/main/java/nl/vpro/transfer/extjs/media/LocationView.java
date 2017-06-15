/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.transfer.extjs.ExtRecord;
import nl.vpro.util.Helper;

import javax.xml.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;
import java.util.TimeZone;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder =
    {
        "id",
        "url",
        "bitrate",
        "format",
        "offset",
        "workflow",
        "ceresPlatform",
        "ceresAuthority"
    })
public class LocationView extends ExtRecord {
    public static final String TIME_FORMAT = "mm:ss.SSS";

    private Long id;

    private String url;

    private Integer bitrate;

    private String format;

    private Duration offset;

    @XmlAttribute
    private Date creationDate;

    @XmlAttribute
    private Date lastModified;

    @XmlAttribute
    private Date publishStart;

    @XmlAttribute
    private Date publishStop;

    @XmlElement(required = false, nillable = false)
    private Platform ceresPlatform;

    @XmlElement(required = true, nillable = false)
    private Boolean ceresAuthority;

    @XmlAttribute
    private Workflow workflow;

    protected LocationView() {
    }

    private LocationView(Long id, String url, Integer bitrate, String format, Workflow workflow, Platform ceresPlatform, Boolean ceresAuthority) {
        this.id = id;
        this.url = url;
        this.bitrate = bitrate;
        this.format = format;
        this.workflow = workflow;
        this.ceresPlatform = ceresPlatform;
        this.ceresAuthority = ceresAuthority;
    }

    public static LocationView create(Location fullLocation) {
        LocationView simpleLocation = new LocationView(
            fullLocation.getId(),
            fullLocation.getProgramUrl(),
            (fullLocation.getAvAttributes() != null) ? fullLocation.getAvAttributes().getBitrate() : null,
            (fullLocation.getAvAttributes() != null) ? fullLocation.getAvAttributes().getAvFileFormat().toString() : null,
            fullLocation.getWorkflow(),
            fullLocation.getPlatform(),
            fullLocation.isAuthorityUpdate()
        );

        simpleLocation.offset = fullLocation.getOffset() == null ? null : fullLocation.getOffset();
        simpleLocation.creationDate = fullLocation.getCreationDate();
        simpleLocation.lastModified = fullLocation.getLastModified();
        simpleLocation.publishStart = fullLocation.getPublishStart();
        simpleLocation.publishStop = fullLocation.getPublishStop();

        return simpleLocation;
    }

    public Location toLocation() {
        Location fullLocation = new Location(this.url, OwnerType.BROADCASTER);

        fullLocation.setId(this.id);

        return updateTo(fullLocation);
    }

    public Location updateTo(Location fullLocation) {
        fullLocation
            .setProgramUrl(this.url)
            .setAvFileFormat(AVFileFormat.valueOf(this.format))
            .setBitrate(this.bitrate)
            .setOffset(this.offset)
            .setPublishStartInstant(this.publishStart.toInstant())
            .setPublishStopInstant(this.publishStop.toInstant());

        fullLocation.setOwner(OwnerType.BROADCASTER);

        return fullLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBitrate() {
        if (bitrate == null) {
            return null;
        }
        return bitrate.toString();
    }

    public void setBitrate(String bitrate) {
        if (Helper.isEmpty(bitrate)) {
            this.bitrate = null;
            return;
        }

        this.bitrate = Integer.parseInt(bitrate);
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getOffset() {
        if (this.offset == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf.format(offset);
    }

    public void setOffset(String offset) {
        if (Helper.isEmpty(offset)) {
            this.offset = null;
            return;
        }

        this.offset = Duration.between(
            LocalTime.MIN,
            LocalTime.parse(offset)
        );
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Date publishStart) {
        this.publishStart = publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Date publishStop) {
        this.publishStop = publishStop;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Platform getCeresPlatform() {
        return ceresPlatform;
    }

    public void setCeresLocation(Platform ceresPlatform) {
        this.ceresPlatform = ceresPlatform;
    }

    public Boolean getCeresAuthority() {
        return ceresAuthority;
    }

    public void setCeresAuthority(Boolean ceresAuthority) {
        this.ceresAuthority = ceresAuthority;
    }
}
