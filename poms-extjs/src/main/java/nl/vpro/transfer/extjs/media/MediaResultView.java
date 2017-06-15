/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.GroupType;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.media.support.MediaTypeView;

import javax.xml.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@XmlRootElement(name = "media")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "id",
    "mid",
    "urn",
    "workflow",
    "broadcasters",
    "title",
    "subTitle",
    "description",
    "mediaType",
    "locations",
    "tags",
    "firstShowing",
    "creationDate",
    "lastModified",
    "createdBy",
    "lastModifiedBy",
    "publishStart",
    "publishStop",
    "sortDate"
})
public class MediaResultView {

    private Long id;

    private String mid;

    private String urn;

    private String workflow;

    private String broadcasters;

    private String title;

    private String subTitle;

    private String description;

    private MediaTypeView mediaType;

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    private List<LocationView> locations = new ArrayList<>();

    @JsonProperty("tags")
    @XmlElement(name = "tag")
    private List<String> tags = new ArrayList<>();

    private ScheduleEventView firstShowing;

    private String creationDate;

    private String lastModified;

    private String createdBy;

    private String lastModifiedBy;

    private String publishStart;

    private String publishStop;

    private String sortDate;


    private MediaResultView() {
    }

    private MediaResultView(Long id, String mid, Workflow workflow, String title, String subTitle, String description, MediaTypeView mediaType) {
        this.id = id;
        this.mid = mid;
        this.workflow = workflow == null ? null : workflow.name();
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.mediaType = mediaType;
    }


    public static MediaResultView create(MediaPermissionEvaluator permissionEvaluator, MediaListItem listItem) {
        MediaResultView simpleMedia = new MediaResultView(
            listItem.getId(),
            listItem.getMid(),
            listItem.getWorkflow(),
            listItem.getTitle(),
            listItem.getSubTitle(),
            listItem.getDescription(),
            MediaTypeView.create(permissionEvaluator, listItem.getType())
        );

        simpleMedia.urn = listItem.getUrn();

        StringBuilder sb = new StringBuilder();
        for (Broadcaster broadcaster : listItem.getBroadcasters()) {
            sb.append(broadcaster.getDisplayName()).append(", ");
        }
        int length = sb.length();
        if (length > 2) {
            simpleMedia.broadcasters = sb.substring(0, length - 2);
        }


        simpleMedia.creationDate = getDate(listItem.getCreationDate());
        simpleMedia.lastModified = getDate(listItem.getLastModified());
        simpleMedia.createdBy = listItem.getCreatedBy() == null ? null : listItem.getCreatedBy().getDisplayName();
        simpleMedia.lastModifiedBy = listItem.getLastModifiedBy() == null ? null : listItem.getLastModifiedBy().getDisplayName();

        simpleMedia.publishStart = getDate(listItem.getPublishStartInstant());
        simpleMedia.publishStop = getDate(listItem.getPublishStopInstant());


        if (listItem.getLocations() != null) {
            for (Location location : listItem.getLocations()) {
                simpleMedia.locations.add(LocationView.create(location));
            }
        }
        simpleMedia.tags = listItem.getTags().stream().map(Tag::getText).collect(Collectors.toList());

        simpleMedia.sortDate = getDate(listItem.getSortDate());

        simpleMedia.firstShowing = ScheduleEventView.createMediaEvent(listItem.getFirstScheduleEvent());


        return simpleMedia;
    }

    private static String getDate(Date date) {
        if (date == null) return null;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return df.format(date);
    }

    private static String getDate(Instant date) {
        if (date == null) return null;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return df.format(date);
    }


    public Long getId() {
        return id;
    }

    public String getMid() {
        return mid;
    }

    public String getUrn() {
        return urn;
    }

    public String getWorkflow() {
        return workflow;
    }

    public String getBroadcasters() {
        return broadcasters;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getDescription() {
        return description;
    }

    public MediaTypeView getMediaType() {
        return mediaType;
    }

    public String getTypeName() {
        return mediaType == null ? null : mediaType.getText();
    }

    public List<LocationView> getLocations() {
        return locations;
    }

    public ScheduleEventView getFirstShowing() {
        return firstShowing;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLastModified() {
        return lastModified;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getPublishStart() {
        return publishStart;
    }

    public String getPublishStop() {
        return publishStop;
    }

    public String getSortDate() {
        return sortDate;
    }

    private static boolean isSeriesOrSeason(MediaObject media) {
        if (!(media instanceof Group)) {
            return false;
        }

        if (((Group) media).getType() == GroupType.SERIES) {
            return true;
        }

        if (((Group) media).getType() == GroupType.SEASON) {
            return true;
        }
        return false;
    }
}
