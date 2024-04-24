package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.user.*;
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
            "creationInstant",
            "lastModifiedInstant",
            "createdByPrincipalId",
            "lastModifiedByPrincipalId",
            "sortDate",
            "mediaType",
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
            "numberOfPublishedLocations",
            "tags",
            "image",
            "streamingPlatformStatus"
        }
)
public class MediaListItem extends PublishableListItem<MediaListItem> implements TrackableMedia {

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

    @XmlAttribute(name = "mediaClass")
    @Getter
    @Setter
    private String mediaClass;

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
    @JsonProperty("broadcasters")
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
    @XmlElement(name = "type")
    private MediaType mediaType;

    @Getter
    @Setter
    //@XmlElement("location") not backwards compatible!
    @JsonProperty("locations")
    private SortedSet<Location> locations;

    @Getter
    @Setter
    private Integer numberOfLocations;


    @Getter
    @Setter
    private Integer numberOfPublishedLocations;

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
    private StreamingStatusImpl streamingPlatformStatus;

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
            this.mediaClass = Program.class.getSimpleName();
            SortedSet<ScheduleEvent> scheduleEvents = ((Program) media).getScheduleEvents();
            if(!scheduleEvents.isEmpty()) {
                this.firstScheduleEvent = ScheduleEvents.getFirstScheduleEvent(scheduleEvents, false).orElse(null);
                this.firstScheduleEventNoRerun = ScheduleEvents.getFirstScheduleEvent(scheduleEvents, true).orElse(null);
                this.lastScheduleEvent= ScheduleEvents.getLastScheduleEvent(scheduleEvents, false).orElse(null);
                this.lastScheduleEventNoRerun = ScheduleEvents.getLastScheduleEvent(scheduleEvents, true).orElse(null);
                this.sortDateScheduleEvent = ScheduleEvents.sortDateEventForProgram(scheduleEvents).orElse(null);
            }
        } else if(media instanceof Group) {
            this.mediaClass = Group.class.getSimpleName();
        } else if(media instanceof Segment) {
            this.mediaClass = Segment.class.getSimpleName();
        } else {
            this.mediaClass = getClass().getName();
        }
        this.mediaType = media.getType().getMediaType();
        this.sortDate = media.getSortInstant();
        this.locations = media.getLocations();
        this.numberOfLocations = media.getLocations().size();
        this.tags = media.getTags();

        this.lastPublished = media.getLastPublishedInstant();


    }

    @Override
    public String getUrn() {
        return (mediaType == null ? "null" : mediaType.getSubType().getUrnPrefix()) + id;
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
    @XmlElement(name = "creationDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getCreationInstant() {
        return super.getCreationInstant();
    }

    @Override
    public void setCreationInstant(Instant creationInstant) {
        super.setCreationInstant(creationInstant);
    }

    @Override
    @XmlElement(name = "lastModified")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getLastModifiedInstant() {
        return super.getLastModifiedInstant();
    }

    @Override
    public void setLastModifiedInstant(Instant lastModifiedInstant) {
        super.setLastModifiedInstant(lastModifiedInstant);
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

    public static final List<String> FIELD_NAMES;
    private static final  List<Field> FIELDS;
    static {
        FIELDS = Arrays.stream(MediaListItem.class.getDeclaredFields()).filter(
            (f) -> {
                f.setAccessible(true);
                return !Modifier.isStatic(f.getModifiers());
            }).toList();
        FIELD_NAMES = FIELDS.stream()
            .map(Field::getName)
            .toList();
    }

    private static Object toRecordObject(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Collection) {
            return ((Collection<?>) o).stream().map( e -> toRecordObject(e).toString()).collect(Collectors.joining(", "));
        }
        if (o instanceof ScheduleEvent) {
            return ((ScheduleEvent) o).getId().toString();
        }
        if (o instanceof Location) {
            Location l = (Location) o;
            return l.getOwner() + ":" + l.getPlatform() + ":" + l.getProgramUrl();
        }
        if (o instanceof ImageListItem) {
            ImageListItem i = (ImageListItem) o;
            return i.getImageUri() + ":" + i.getLicense();
        }
        if (o instanceof Number || o instanceof Boolean || o.getClass().isPrimitive()) {
            return o;
        }
        if (o instanceof Organization) {
            return ((Organization) o).getId();
        }
        return o.toString();
    }

    public Object[] asRecord() {
        return FIELDS.stream().map(f ->{
            try {
                return toRecordObject(f.get(MediaListItem.this));
            } catch (IllegalAccessException e) {
                return e.getMessage();
            }
        }).toArray(Object[]::new);

    }
}
