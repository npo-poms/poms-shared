/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.jackson2.DurationToJsonTimestamp;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.transfer.extjs.ExtRecord;
import nl.vpro.util.Helper;
import nl.vpro.xml.bind.DurationXmlAdapter;
import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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

    private String offset;

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

        if (fullLocation.getOffset()!= null) {
            final long durationMillis = fullLocation.getOffset().toMillis();
            simpleLocation.offset = DurationFormatUtils.formatDuration(durationMillis, TIME_FORMAT);
        }
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

        Duration offsetDuration = Duration.between(
            LocalTime.MIN,
            LocalTime.parse(this.offset)
        );
        fullLocation
            .setProgramUrl(this.url)
            .setAvFileFormat(AVFileFormat.valueOf(this.format))
            .setBitrate(this.bitrate)
            .setOffset(offsetDuration)
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
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
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
