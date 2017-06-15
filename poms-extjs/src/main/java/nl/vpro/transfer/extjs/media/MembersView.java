/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.search.MemberRefItem;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.ExtRecord;
import nl.vpro.transfer.extjs.media.support.MediaTypeView;

import javax.xml.bind.annotation.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "id",
    "memberId",
    "mid",
    "workflow",
    "title",
    "subtitle",
    "locations",
    "firstShowing",
    "lastModified",
    "creationDate",
    "broadcasters",
    "mediaType",
    "number",
    "added",
    "highlighted",
    "publishStart",
    "publishStop",
    "sortDate"
})
public class MembersView extends ExtRecord {

    private Long id;

    private Long memberId;

    private String mid;

    private String workflow;

    private String title;

    private String subtitle;

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    private List<LocationView> locations;

    private ScheduleEventView firstShowing;

    private Date lastModified;

    private MediaTypeView mediaType;

    private Integer number;

    private Instant added;

    private Instant publishStart;

    private Instant publishStop;

    private Date creationDate;

    private Date sortDate;

    private Boolean highlighted;

    private List<String> broadcasters;

    private MembersView() {
    }

    private MembersView(MediaPermissionEvaluator permissionEvaluator, MemberRefItem item) {
        this.id = item.getId();
        MediaListItem member = item.getMember();
        if (member != null) {
            this.memberId = member.getId();
            this.mid = member.getMid();
            this.workflow = member.getWorkflow().name();
            this.title = member.getTitle();
            this.subtitle = member.getSubTitle();
            SortedSet<Location> tempLocations = member.getLocations();
            if (tempLocations != null) {
                this.locations = new ArrayList<>();
                for (Location location : tempLocations) {
                    this.locations.add(LocationView.create(location));
                }
            }
            ScheduleEvent first = member.getFirstScheduleEvent();
            if (first != null) {
                this.firstShowing = ScheduleEventView.createMediaEvent(first);
            }

            this.lastModified = member.getLastModified();
            this.mediaType = MediaTypeView.create(permissionEvaluator, member.getType());
            this.creationDate = member.getCreationDate();
            this.publishStart = member.getPublishStartInstant();
            this.publishStop = member.getPublishStopInstant();
            this.sortDate = member.getSortDate();
            this.broadcasters = new ArrayList<>();
            for (Broadcaster b : member.getBroadcasters()) {
                broadcasters.add(b.getId());
            }
        }
        this.number = item.getNumber();
        this.added = item.getAdded();
        this.highlighted = item.getHighlighted();
    }

    public static MembersView create(MediaPermissionEvaluator permissionEvaluator, MemberRefItem memberRef) {
        return new MembersView(permissionEvaluator, memberRef);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMid() {
        return mid;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumber() {
        if(number == null || number < 1) {
            return null;
        }
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Instant getAdded() {
        return added;
    }

    public void setAdded(Instant added) {
        this.added = added;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<LocationView> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationView> locations) {
        this.locations = locations;
    }

    public ScheduleEventView getFirstShowing() {
        return firstShowing;
    }

    public void setFirstShowing(ScheduleEventView firstShowing) {
        this.firstShowing = firstShowing;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public MediaTypeView getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaTypeView type) {
        this.mediaType = type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getBroadcasters() {
        return broadcasters;
    }

    public void setBroadcasters(List<String> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public Instant getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Instant publishStart) {
        this.publishStart = publishStart;
    }

    public Instant getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Instant publishStop) {
        this.publishStop = publishStop;
    }

    public Date getSortDate() {
        return sortDate;
    }
}
