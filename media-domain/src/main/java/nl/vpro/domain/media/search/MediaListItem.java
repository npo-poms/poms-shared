package nl.vpro.domain.media.search;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.ThirdParty;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * Represents the result of a search-action. I.e. a short representation of a media object.
 *
 * @author Michiel Meeuwissen
 * @since 1.5
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "item")
@XmlType(
    name = "mediaListItem",
    propOrder =
        {
            "broadcasters",
            "title",
            "subTitle",
            "description",
            "creationDate",
            "lastModified",
            "createdByPrincipalId",
            "lastModifiedByPrincipalId",
            "sortDate",
            "type",
            "publishStartInstant",
            "publishStopInstant",
            "lastPublished",
            "firstScheduleEvent",
            "locations",
            "numberOfLocations",
            "tags",
            "image"
        }
)
public class MediaListItem extends PublishableListItem {

    @XmlAttribute
    private String mid;

    @XmlAttribute
    private AVType avType;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant lastPublished;

    @XmlAttribute
    private String mediaType;

    @XmlAttribute
    private Boolean episodesLocked;

    private String description;

    private String title;

    private String subTitle;

    @XmlTransient
    private String lastModifiedByHolder;

    @XmlTransient
    private String createdByHolder;


    @XmlElement(name = "broadcaster")
    private List<Broadcaster> broadcasters;

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private SortedSet<Tag> tags;

    @XmlTransient
    private List<Portal> portals;

    @XmlTransient
    private List<ThirdParty> thirdParties;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant sortDate;

    private MediaType type;

    private SortedSet<Location> locations;

    private Integer numberOfLocations;

    private ImageListItem image;

    private ScheduleEvent firstScheduleEvent;

    public MediaListItem() {
    }

    public MediaListItem(MediaObject media) {
        super(media);

        setUrn(media.getUrn());

        this.mid = media.getMid();
        this.avType = media.getAVType();

        this.title = media.getMainTitle();
        this.subTitle = media.getSubTitle();

        this.description = media.getMainDescription();

        this.broadcasters = Collections.unmodifiableList(media.getBroadcasters());
        this.portals = Collections.unmodifiableList(media.getPortals());
        this.thirdParties = Collections.unmodifiableList(media.getThirdParties());

        if(media instanceof Program) {
            // proxy's...
            this.mediaType = Program.class.getName();
        } else if(media instanceof Group) {
            this.mediaType = Group.class.getName();
        } else if(media instanceof Segment) {
            this.mediaType = Segment.class.getName();
        } else {
            this.mediaType = getClass().getName();
        }
        this.type = media.getType().getMediaType();
        this.sortDate = media.getSortInstant();
        this.locations = media.getLocations();
        this.numberOfLocations = media.getLocations().size();
        this.tags = media.getTags();

        this.lastPublished = media.getLastPublishedInstant();

        if(media.getScheduleEvents().size() > 0) {
            this.firstScheduleEvent = media.getScheduleEvents().first();
        }
    }

    @Override
    public String getUrn() {
        return (type == null ? "null" : type.getSubType().getUrnPrefix()) + id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public AVType getAvType() {
        return avType;
    }

    public void setAvType(AVType avType) {
        this.avType = avType;
    }

    @Override
    public Instant getLastPublished() {
        return lastPublished;
    }

    @Override
    public void setLastPublished(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }



    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Boolean getEpisodesLocked() {
        return episodesLocked;
    }

    public void setEpisodesLocked(Boolean episodesLocked) {
        this.episodesLocked = episodesLocked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<Broadcaster> getBroadcasters() {
        return broadcasters;
    }

    public void setBroadcasters(List<Broadcaster> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public void setPortals(List<Portal> portals) {
        this.portals = portals;
    }

    public List<ThirdParty> getThirdParties() {
        return thirdParties;
    }

    public void setThirdParties(List<ThirdParty> thirdParties) {
        this.thirdParties = thirdParties;
    }

    public Instant getSortDate() {
        return sortDate;
    }

    public void setSortDate(Instant sortDate) {
        this.sortDate = sortDate;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public SortedSet<Location> getLocations() {
        return locations;
    }

    public void setLocations(SortedSet<Location> locations) {
        this.locations = locations;
    }

    public SortedSet<Tag> getTags() {
        return tags;
    }

    public void setTags(SortedSet<Tag> tags) {
        this.tags = tags;
    }

    public ImageListItem getImage() {
        return image;
    }

    public void setImage(ImageListItem image) {
        this.image = image;
    }

    public ScheduleEvent getFirstScheduleEvent() {
        return firstScheduleEvent;
    }

    public void setFirstScheduleEvent(ScheduleEvent firstScheduleEvent) {
        this.firstScheduleEvent = firstScheduleEvent;
    }

    @XmlElement(name = "createdBy")
    public String getCreatedByPrincipalId() {
        return createdBy == null ? createdByHolder : createdBy.getPrincipalId();
    }

    public void setCreatedByPrincipalId(String principalId) {
        createdByHolder = principalId;
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant  getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    public void setCreationDate(Instant creationDate) {
        super.setCreationDate(creationDate);
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getLastModified() {
        return super.getLastModified();
    }

    @Override
    public void setLastModified(Instant lastModified) {
        super.setLastModified(lastModified);
    }

    @XmlElement(name = "lastModifiedBy")
    public String getLastModifiedByPrincipalId() {
        return lastModifiedBy == null ? lastModifiedByHolder : lastModifiedBy.getPrincipalId();
    }

    public void setLastModifiedByPrincipalId(String principalId) {
        lastModifiedByHolder = principalId;
    }

    @Override
    @XmlElement(name = "publishStart")
    @JsonProperty("publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStartInstant() {
        return super.getPublishStartInstant();
    }

    @Override
    public MediaListItem setPublishStartInstant(Instant stop) {
        return (MediaListItem) super.setPublishStartInstant(stop);
    }

    @Override
    @XmlElement(name = "publishStop")
    @JsonProperty("publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStopInstant() {
        return super.getPublishStopInstant();
    }

    @Override
    public MediaListItem setPublishStopInstant(Instant stop) {
        return (MediaListItem) super.setPublishStopInstant(stop);
    }


    public Integer getNumberOfLocations() {
        return numberOfLocations;
    }

    public void setNumberOfLocations(Integer numberOfLocations) {
        this.numberOfLocations = numberOfLocations;
    }

    @Override
    public String toString() {
        return mid + " " + title;
    }

}
