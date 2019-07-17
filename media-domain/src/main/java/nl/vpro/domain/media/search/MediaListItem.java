package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.StreamingStatus;
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
            "firstScheduleEventNoRerun",
            "lastScheduleEvent",
            "lastScheduleEventNoRerun",
            "sortDateScheduleEvent",
            "locations",
            "numberOfLocations",
            "tags",
            "image",
            "streamingPlatformStatus"
        }
)
public class MediaListItem extends PublishableListItem {

    @XmlAttribute
    @Getter
    @Setter
    private String mid;

    @XmlAttribute
    @Getter
    @Setter
    private AVType avType;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant lastPublished;

    @XmlAttribute
    @Getter
    @Setter
    private String mediaType;

    @XmlAttribute
    @Getter
    @Setter
    private Boolean episodesLocked;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String subTitle;

    @XmlTransient
    private String lastModifiedByHolder;

    @XmlTransient
    private String createdByHolder;


    @XmlElement(name = "broadcaster")
    @Getter
    @Setter
    private List<Broadcaster> broadcasters;

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Getter
    @Setter
    private SortedSet<Tag> tags;

    @XmlTransient
    @Getter
    @Setter
    private List<Portal> portals;

    @XmlTransient
    @Getter
    @Setter
    private List<ThirdParty> thirdParties;

    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @Getter
    @Setter
    private Instant sortDate;

    @Getter
    @Setter
    private MediaType type;

    @Getter
    @Setter
    private SortedSet<Location> locations;

    @Getter
    @Setter
    private Integer numberOfLocations;

    @Getter
    @Setter
    private ImageListItem image;

    @Getter
    @Setter
    private ScheduleEvent firstScheduleEvent;

    @Getter
    @Setter
    private ScheduleEvent firstScheduleEventNoRerun;

    @Getter
    @Setter
    private ScheduleEvent lastScheduleEvent;


    @Getter
    @Setter
    private ScheduleEvent lastScheduleEventNoRerun;

    @Getter
    @Setter
    private ScheduleEvent sortDateScheduleEvent;

    @Getter
    @Setter
    private StreamingStatus streamingPlatformStatus;

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
            this.firstScheduleEvent = ScheduleEvents.getFirstScheduleEvent(media.getScheduleEvents(), false).orElse(null);
            this.firstScheduleEventNoRerun = ScheduleEvents.getFirstScheduleEvent(media.getScheduleEvents(), true).orElse(null);
            this.lastScheduleEvent= ScheduleEvents.getLastScheduleEvent(media.getScheduleEvents(), false).orElse(null);
            this.lastScheduleEventNoRerun = ScheduleEvents.getLastScheduleEvent(media.getScheduleEvents(), true).orElse(null);
            this.sortDateScheduleEvent = ScheduleEvents.sortDateEventForProgram(media.getScheduleEvents()).orElse(null);
        }
    }

    @Override
    public String getUrn() {
        return (type == null ? "null" : type.getSubType().getUrnPrefix()) + id;
    }


    @Override
    public Instant getLastPublished() {
        return lastPublished;
    }

    @Override
    public void setLastPublished(Instant lastPublished) {
        this.lastPublished = lastPublished;
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

    @NonNull
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

    @NonNull
    @Override
    public MediaListItem setPublishStopInstant(Instant stop) {
        return (MediaListItem) super.setPublishStopInstant(stop);
    }



    @Override
    public String toString() {
        return mid + " " + title;
    }

}
