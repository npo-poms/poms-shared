/**
 /**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.LocationAuthorityRecord;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.media.support.AVTypeView;
import nl.vpro.transfer.extjs.media.support.MediaTypeView;
import nl.vpro.transfer.extjs.media.support.OwnerTypeView;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "id",
    "mid",
    "urn",
    "writable",
    "broadcasters",
    "portals",
    "portalRestrictions",
    "geoRestrictions",
    "type",
    "avType",
    "ownerTypes",
    "allowEditGenre",
    "genres",
    "tags",
    "authorizedDuration",
    "duration",
    "releaseYear",
    "ageRating",
    "contentRatings",
    "firstShowing",
    "locations",
    "members",
    "memberOf",
    "images",
    "person",
    "persons",
    "website",
    "websites",
    "twitterHash",
    "twitterAccount",
    "relations",
    "isEmbeddable",
    "workflow",
    "publishStart",
    "publishStop",
    "creationDate",
    "lastModifiedDate",
    "createdBy",
    "lastModifiedBy",
    "sortDate",
    "allowEditGeoRestrictions",
    "allowEditNLRestrictionOnly",
    "ceresAuthority",
    "ceresRecord"
})
public abstract class MediaEditView extends MediaView {

    private Long id;

    private String mid;

    private String urn;

    private boolean writable;


    private List<OrganizationView> broadcasters = new ArrayList<>();

    private List<OrganizationView> portals = new ArrayList<>();

    private List<RestrictionView> portalRestrictions = new ArrayList<>();

    private List<RestrictionView> geoRestrictions = new ArrayList<>();

    private MediaTypeView type;

    private AVTypeView avType;

    private OwnerTypeView[] ownerTypes;

    boolean allowEditGenre = false;

    @XmlElement(name = "genre")
    private List<String> genres = new ArrayList<>();

    @XmlElement(name = "tag")
    private List<String> tags = new ArrayList<>();

    private boolean authorizedDuration;

    private String duration;

    private Short releaseYear;

    private AgeRating ageRating;

    @XmlElement(name = "contentRating")
    private List<ContentRating> contentRatings = new ArrayList<>();

    private ScheduleEventView firstShowing;

    private int locations;

    private long members;

    private int memberOf;

    private int images;

    private String person;

    private int persons;

    private String website;

    private int relations;

    private int websites;

    private String twitterHash;

    private String twitterAccount;

    @XmlElement(name = "embeddable")
    private boolean isEmbeddable;

    private String workflow;

    private Date publishStart;

    private Date publishStop;

    private Date creationDate;

    private Date lastModifiedDate;

    private String createdBy;

    private String lastModifiedBy;

    private Date sortDate;

    private boolean allowEditGeoRestrictions;

    private boolean allowEditNLRestrictionOnly;

    @XmlElement(required = false, nillable = false)
    private Boolean ceresAuthority;

    @XmlElement(required = true, nillable = false)
    private Boolean ceresRecord;

    protected MediaEditView() {
    }

    public static MediaEditView create(
        MediaPermissionEvaluator permissionEvaluator,
        MediaObject fullMedia,
        long episodeCount,
        long memberCount,
        boolean writable,
        Collection<Broadcaster> allBroadcasters,
        Collection<Portal> allowedPortals,
        boolean mayEditGenre) {
        MediaEditView mediaEditView;
        if(fullMedia instanceof Program) {
            mediaEditView = ProgramEditView.create((Program)fullMedia);
        } else if(fullMedia instanceof Group) {
            mediaEditView = GroupEditView.create((Group)fullMedia, episodeCount);
        } else if(fullMedia instanceof Segment) {
            mediaEditView = SegmentEditView.create((Segment)fullMedia);
        } else {
            throw new UnsupportedOperationException(fullMedia.getClass().getSimpleName() + " is not supported.");
        }

        mediaEditView.init(fullMedia);

        mediaEditView.id = fullMedia.getId();
        mediaEditView.mid = fullMedia.getMid();
        mediaEditView.urn = fullMedia.getUrn();


        {
            mediaEditView.writable = writable;
            List<Broadcaster> activeBroadcasters = fullMedia.getBroadcasters();

            // remove the active ones as they need to be on top
            for(Broadcaster currBroadcaster : activeBroadcasters) {
                allBroadcasters.remove(currBroadcaster);
            }

            // add the active broadcasters in correct order
            for(Broadcaster broadcaster : activeBroadcasters) {
                mediaEditView.broadcasters.add(OrganizationView.create(broadcaster, true));
            }

            // add the non selected broadcasters
            for(Broadcaster broadcaster : allBroadcasters) {
                mediaEditView.broadcasters.add(OrganizationView.create(broadcaster, false));
            }
        }

        {
            List<Portal> activePortals = fullMedia.getPortals();
            SortedSet<Portal> allPortals = activePortals == null ?
                new TreeSet<>() :
                new TreeSet<>(activePortals);
            if(allowedPortals != null) {
                allPortals.addAll(allowedPortals);
            }
            for(Portal portal : allPortals) {
                mediaEditView.portals.add(OrganizationView.create(portal,
                    activePortals != null &&
                        activePortals.contains(portal)
                ));
            }
        }

        {
            for(PortalRestriction restriction : fullMedia.getPortalRestrictions()) {
                RestrictionView view = RestrictionView.create(restriction);
                mediaEditView.getPortalRestrictions().add(view);
            }
        }

        {
            for(GeoRestriction restriction : fullMedia.getGeoRestrictions()) {
                RestrictionView view = RestrictionView.create(restriction);
                mediaEditView.getGeoRestrictions().add(view);
            }
        }

        mediaEditView.type = MediaTypeView.create(permissionEvaluator, fullMedia.getType().getMediaType());
        mediaEditView.avType = AVTypeView.create(fullMedia.getAVType());
        mediaEditView.ownerTypes = OwnerTypeView.create(MediaObjects.findOwnersForTextFields(fullMedia));

        for(Genre genre : fullMedia.getGenres()) {
            mediaEditView.genres.add(genre.getTermId());
        }
        mediaEditView.allowEditGenre = mayEditGenre;

        for(Tag tag : fullMedia.getTags()) {
            mediaEditView.tags.add(tag.getText());
        }

        mediaEditView.authorizedDuration = fullMedia.hasAuthorizedDuration();

        if(fullMedia.getDurationAsDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            mediaEditView.duration = sdf.format(fullMedia.getDurationAsDate());
        }

        mediaEditView.releaseYear = fullMedia.getReleaseYear();
        mediaEditView.ageRating = fullMedia.getAgeRating();

        for(ContentRating contentRating : fullMedia.getContentRatings()) {
            mediaEditView.contentRatings.add(contentRating);
        }

        if(!fullMedia.getScheduleEvents().isEmpty()) {
            mediaEditView.firstShowing = ScheduleEventView.createMediaEvent(fullMedia.getScheduleEvents().first());
        }

        mediaEditView.locations = fullMedia.getLocations().size();
        mediaEditView.members = memberCount;
        mediaEditView.memberOf = fullMedia.getMemberOf().size();
        mediaEditView.images = fullMedia.getImages().size();

        mediaEditView.persons = fullMedia.getPersons().size();
        if(mediaEditView.persons > 0) {
            Person p = fullMedia.getPersons().get(0);
            mediaEditView.person = p.getGivenName() + " " + p.getFamilyName();
        }

        mediaEditView.websites = fullMedia.getWebsites().size();
        mediaEditView.relations = fullMedia.getRelations().size();
        for(Website website : fullMedia.getWebsites()) {
            // Tmp fix for MSE-2189
            if(website != null) {
                mediaEditView.website = website.getUrl();
                break;
            }
        }

        mediaEditView.twitterHash = TwitterRef.getValueOrNull(MediaObjects.getTwitterHash(fullMedia));
        mediaEditView.twitterAccount = TwitterRef.getValueOrNull(MediaObjects.getTwitterAccount(fullMedia));

        mediaEditView.isEmbeddable = fullMedia.isEmbeddable();

        mediaEditView.workflow = fullMedia.getWorkflow().name();

        mediaEditView.publishStart = fullMedia.getPublishStart();
        mediaEditView.publishStop = fullMedia.getPublishStop();

        mediaEditView.creationDate = fullMedia.getCreationDate();
        mediaEditView.lastModifiedDate = fullMedia.getLastModified();

        mediaEditView.createdBy = fullMedia.getCreatedBy().getDisplayName();
        mediaEditView.lastModifiedBy = fullMedia.getLastModifiedBy().getDisplayName();

        mediaEditView.sortDate = fullMedia.getSortDate();

        LocationAuthorityRecord locationAuthorityRecord = fullMedia.getLocationAuthorityRecord(Platform.INTERNETVOD);

        mediaEditView.allowEditNLRestrictionOnly = locationAuthorityRecord != null; // if we have a record, only edit of NL is allowed
        mediaEditView.allowEditGeoRestrictions = true;
        SortedSet<ScheduleEvent> scheduleEvents = fullMedia.getScheduleEvents();
        for(ScheduleEvent scheduleEvent : scheduleEvents) {
            if(Channel.NED1.equals(scheduleEvent.getChannel()) ||
                Channel.NED2.equals(scheduleEvent.getChannel()) ||
                Channel.NED3.equals(scheduleEvent.getChannel())) {
                mediaEditView.allowEditGeoRestrictions = false; // ned1-3 isn't allowed to be editted
                break;
            }
        }

        mediaEditView.ceresAuthority = locationAuthorityRecord != null && locationAuthorityRecord.hasAuthority();
        mediaEditView.ceresRecord = locationAuthorityRecord != null;

        return mediaEditView;
    }

    public Long getId() {
        return id;
    }

    public String getUrn() {
        return urn;
    }

    public boolean isWritable() {
        return writable;
    }

    public List<OrganizationView> getBroadcasters() {
        return broadcasters;
    }


    public List<OrganizationView> getPortals() {
        return portals;
    }

    public List<RestrictionView> getPortalRestrictions() {
        return portalRestrictions;
    }

    public void setExclusives(List<RestrictionView> portalRestrictions) {
        this.portalRestrictions = portalRestrictions;
    }

    public List<RestrictionView> getGeoRestrictions() {
        return geoRestrictions;
    }

    public void setRegions(List<RestrictionView> geoRestrictions) {
        this.geoRestrictions = geoRestrictions;
    }

    public String getType() {
        return type.getType();
    }

    public String getTypeName() {
        return type.getText();
    }

    public AVTypeView getAvType() {
        return avType;
    }

    public OwnerTypeView[] getOwnerTypes() {
        return ownerTypes;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isAuthorizedDuration() {
        return authorizedDuration;
    }

    /**
     * Returns a formatted duration string according to HH:mm:ss.SSS.
     */
    public String getDuration() {
        return duration;
    }

    public Short getReleaseYear() {
        return releaseYear;
    }

    public AgeRating getAgeRating() {
        return ageRating;
    }

    public List<ContentRating> getContentRatings() {
        return contentRatings;
    }

    public ScheduleEventView getFirstShowing() {
        return firstShowing;
    }

    public int getLocations() {
        return locations;
    }

    public long getMembers() {
        return members;
    }

    public int getMemberOf() {
        return memberOf;
    }

    public int getImages() {
        return images;
    }

    public String getPerson() {
        return person;
    }

    public int getPersons() {
        return persons;
    }

    public String getWebsite() {
        return website;
    }

    public int getWebsites() {
        return websites;
    }

    public String getTwitterHash() {
        return twitterHash;
    }

    public String getTwitterAccount() {
        return twitterAccount;
    }

    public int getRelations() {
        return relations;
    }

    public boolean isEmbeddable() {
        return isEmbeddable;
    }

    public String getWorkflow() {
        return workflow;
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Date getSortDate() {
        return sortDate;
    }

    public String getMid() {
        return mid;
    }

    public boolean isAllowEditGeoRestrictions() {
        return allowEditGeoRestrictions;
    }

    public boolean isAllowEditNLRestrictionOnly() {
        return allowEditNLRestrictionOnly;
    }

    public boolean isAllowEditGenre() {
        return allowEditGenre;
    }

    public Boolean getCeresAuthority() {
        return ceresAuthority;
    }

    public Boolean getCeresRecord() {
        return ceresRecord;
    }
}
