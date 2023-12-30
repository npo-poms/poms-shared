package nl.vpro.domain.media;

import lombok.Singular;

import java.io.Serial;
import java.io.Serializable;
import java.time.*;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.time.DurationMin;
import org.meeuw.functional.TriFunction;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.domain.*;
import nl.vpro.domain.media.bind.NetToString;
import nl.vpro.domain.media.support.*;
import nl.vpro.jackson2.*;
import nl.vpro.persistence.LocalDateToDateConverter;
import nl.vpro.xml.bind.*;

import static javax.persistence.CascadeType.ALL;
import static nl.vpro.domain.TextualObjects.sorted;


@Entity
@IdClass(ScheduleEventIdentifier.class)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FilterDef(name = MediaObjectFilters.DELETED_FILTER)
@Filter(name = MediaObjectFilters.DELETED_FILTER, condition = "(select m.workflow from mediaobject m where m.id = mediaobject_id and m.mergedTo_id is null) NOT IN ('MERGED', 'DELETED')")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventType", propOrder = {
    "titles",
    "descriptions",
    "repeat",
    "memberOf",
    "avAttributes",
    "textSubtitles",
    "textPage",
    "guideDate",
    "startInstant",
    "offset",
    "duration",
    "poProgID",
    "poSeriesIDLegacy",
    "primaryLifestyle",
    "secondaryLifestyle"
})
@JsonPropertyOrder({
    "titles",
    "descriptions",
    "channel",
    "startInstant",
    "guideDate",
    "duration",
    "midRef",
    "poProgID",
    "repeat",
    "memberOf",
    "avAttributes",
    "textSubtitles",
    "textPage",
    "offset",
    "poSeriesIDLegacy",
    "primaryLifestyle",
    "secondaryLifestyle"
})
public class ScheduleEvent implements Serializable, Identifiable<ScheduleEventIdentifier>,
    Comparable<ScheduleEvent>,
    TextualObject<ScheduleEventTitle, ScheduleEventDescription, ScheduleEvent>,
    Child<Program> {

    @Serial
    private static final long serialVersionUID = 2107980433596776633L;
    @Id
    @Enumerated(EnumType.STRING)
    @NotNull
    protected Channel channel;

    @Id
    @NotNull
    protected Instant start;

    @ManyToOne
    @Valid
    protected Net net;

    @Column(nullable = false, name = "guideDay", columnDefinition="Date")
    @Convert(converter = LocalDateToDateConverter.class)
    protected LocalDate guideDay;

    @Embedded
    protected Repeat repeat;

    protected String memberOf;

    @OneToOne(orphanRemoval = true, cascade = ALL)
    protected AVAttributes avAttributes;

    protected String textSubtitles;

    protected String textPage;

    @Column(name = "start_offset")
    @JsonSerialize(using = DurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Duration offset;

    @JsonSerialize(using = DurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = DurationToJsonTimestamp.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @DurationMin // negative durations don't make sense
    protected Duration duration;

    protected String imi;

    @Transient
    protected String urnRef;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonBackReference
    protected Program mediaObject;

    @Enumerated(EnumType.STRING)
    protected ScheduleEventType type;

    @Embedded
    @Column(name = "primary")
    @XmlElement
    protected Lifestyle primaryLifestyle;

    @Embedded
    @Column(name = "secondary")
    @XmlElement
    protected SecondaryLifestyle secondaryLifestyle;

    @Transient
    protected String midRef;

    protected String poSeriesID;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = ALL)
    @Valid
    @XmlElement(name = "title")
    @JsonProperty("titles")
    protected Set<@Valid @NotNull ScheduleEventTitle> titles = new TreeSet<>();


    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = ALL)
    @Valid
    @XmlElement(name = "description")
    @JsonProperty("descriptions")
    protected Set<@Valid @NotNull ScheduleEventDescription> descriptions = new TreeSet<>();

    public ScheduleEvent() {
    }

    public ScheduleEvent(Channel channel, Instant start, Duration duration) {
        this(channel, null, guideLocalDate(start), start, duration, null);
    }

    public ScheduleEvent(Channel channel, Net net, Instant start, Duration duration) {
        this(channel, net, guideLocalDate(start), start, duration, null);
    }

    public ScheduleEvent(Channel channel, LocalDate guideDay, Instant start, Duration duration) {
        this(channel, null, guideDay, start, duration, null);
    }

     public ScheduleEvent(Channel channel, LocalDateTime start, Duration duration) {
        this(channel, null, Schedule.guideDay(start), start.atZone(Schedule.ZONE_ID).toInstant(), duration, null);
    }

    public ScheduleEvent(Channel channel, Net net, LocalDate guideDay, Instant start, Duration duration) {
        this(channel, net, guideDay, start, duration, null);
    }

    public ScheduleEvent(Channel channel, Instant start, Duration duration, Program media) {
        this(channel, null, guideLocalDate(start), start, duration, media);
    }

    public ScheduleEvent(Channel channel, Net net, Instant start, Duration duration, Program media) {
        this(channel, net, guideLocalDate(start), start, duration, media);
    }

    public ScheduleEvent(
        @NonNull  Channel channel,
        @Nullable Net net,
        @Nullable LocalDate guideDay,
        @NonNull  Instant start,
        @NonNull  Duration duration,
        @Nullable Program media) {
        this(channel, net, guideDay, start, duration, null, media, null, null, null, null, null, null, null, null);
    }

    @lombok.Builder(builderClassName = "Builder")
    private ScheduleEvent(
        @NonNull Channel channel,
        @Nullable  Net net,
        @Nullable  LocalDate guideDay,
        @NonNull  Instant start,
        @NonNull  Duration duration,
        String midRef,
        @Nullable Program media,
        @Nullable Repeat repeat,
        @Nullable Lifestyle primaryLifestyle,
        @Nullable SecondaryLifestyle secondaryLifestyle,
        @Singular Set<ScheduleEventTitle> titles,
        @Singular Set<ScheduleEventDescription> descriptions,
        @Nullable AVAttributes avAttributes,
        @Nullable String textPage,
        @Nullable String textSubtitles
        ) {
        this.channel = channel;
        this.net = net;
        this.guideDay = guideDay == null ? guideLocalDate(start) : guideDay;
        this.start = start;
        this.duration = duration;
        this.repeat = Repeat.nullIfDefault(repeat);
        this.midRef = midRef;
        this.primaryLifestyle = primaryLifestyle;
        this.secondaryLifestyle = secondaryLifestyle;
        if (titles != null) {
            for (ScheduleEventTitle st : titles) {
                st.setParent(this);
                this.titles.add(st);
            }
        }

        if (descriptions != null) {
            for (ScheduleEventDescription sd : descriptions) {
                sd.setParent(this);
                this.descriptions.add(sd);
            }
        }
        this.avAttributes = avAttributes;
        this.textPage = textPage;
        this.textSubtitles = textSubtitles;
        setParent(media);
    }


    public ScheduleEvent(ScheduleEvent source) {
        this(source, source.mediaObject);
    }

    public ScheduleEvent(ScheduleEvent source, Program parent) {
        this.channel = source.channel;
        this.net = source.net;
        this.start = source.start;
        this.guideDay = source.guideDay;
        this.repeat = Repeat.copy(source.repeat);
        this.memberOf = source.memberOf;
        this.avAttributes = AVAttributes.copy(source.avAttributes);
        this.textSubtitles = source.textSubtitles;
        this.textPage = source.textPage;
        this.offset = source.offset;
        this.duration = source.duration;
        this.imi = source.imi;
        this.urnRef = source.urnRef;
        this.type = source.type;
        this.primaryLifestyle = Lifestyle.copy(source.primaryLifestyle);
        this.secondaryLifestyle = SecondaryLifestyle.copy(source.secondaryLifestyle);
        this.midRef = source.midRef;
        this.poSeriesID = source.poSeriesID;

        this.mediaObject = parent;
    }

    public static ScheduleEvent copy(ScheduleEvent source) {
        if (source == null) {
            return null;
        }
        return copy(source, source.mediaObject);
    }

    public static ScheduleEvent copy(ScheduleEvent source, Program parent) {
        if (source == null) {
            return null;
        }

        return new ScheduleEvent(source, parent);
    }

    public static ScheduleEvent of(Instant start) {
        return ScheduleEvent.builder().start(start).build();
    }

    private static Date guideDay(Date start) {
        if (start == null) {
            return null;
        }
        return Date.from(guideLocalDate(start).atTime(Schedule.START_OF_SCHEDULE).atZone(Schedule.ZONE_ID).toInstant());
    }

    private static LocalDate guideLocalDate(Date start) {
        if (start == null) {
            return null;
        }
        return guideLocalDate(start.toInstant());
    }

    private static LocalDate guideLocalDate(Instant start) {
        return Schedule.guideDay(start);
    }

    private static Duration duration(Date duration) {
        if (duration == null) {
            return null;
        }
        return Duration.ofMillis(duration.getTime());
    }

    private static Instant instant(Date instant) {
        if (instant == null) {
            return null;
        }
        return instant.toInstant();
    }

    @XmlElement
    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat value) {
        this.repeat = value;
    }
    @JsonView({Views.Publisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean isRerun() {
        if (repeat == null) {
            return false;
        } else {
            return repeat.isRerun;
        }
    }


    @XmlElement
    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String value) {
        this.memberOf = value;
    }

    /**
     * I think in principle some av-attributes (like the aspect ratio) may vary for different schedule events.
     */
    @XmlElement
    public AVAttributes getAvAttributes() {
        return avAttributes;
    }

    public void setAvAttributes(AVAttributes value) {
        this.avAttributes = value;
    }

    @XmlElement
    public String getTextSubtitles() {
        return textSubtitles;
    }

    public void setTextSubtitles(String value) {
        this.textSubtitles = value;
    }

    @XmlElement
    public String getTextPage() {
        return textPage;
    }

    public void setTextPage(String textPage) {
        this.textPage = textPage;
    }


    @XmlElement(name = "guideDay")
    @XmlJavaTypeAdapter(ZonedLocalDateXmlAdapter.class)
    @XmlSchemaType(name = "date")
    @JsonDeserialize(using = StringZonedLocalDateToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringZonedLocalDateToJsonTimestamp.Serializer.class)
    public LocalDate getGuideDate() {
        return guideDay;
    }

    public void setGuideDate(LocalDate guideDate) {
        this.guideDay = guideDate;
    }

    @XmlElement(name = "start")
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getStartInstant() {
        return start;
    }
    public void setStartInstant(Instant start) {
        this.start = start;
    }


    @JsonView({Views.Publisher.class})
    // Because of other 'start' fields (e.g. in segment, it is mapped to _long_). This field is mapped to date in ES. In ES fields with same name must have same mapping.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "eventStart")
    protected Instant getEventStart() {
        return getStartInstant();
    }

    public Instant getStopInstant() {
        return start.plus(getDuration());
    }

    public void setStopInstant(Instant stop) {
        this.duration = Duration.between(start, stop);
    }


    @XmlTransient
    public Instant getRealStartInstant() {
        if (start == null) {
            return null;
        }

        if (offset == null) {
            return start;
        }

        return start.plus(offset);
    }

    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonIgnore
    public Duration getOffset() {
        return offset;
    }

    public void setOffset(Duration offset) {
        this.offset = offset;
    }


    /**
     * @since 4.3
     */
    @XmlElement(name = "duration")
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonIgnore
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration value) {
        this.duration = value;
    }

    @XmlAttribute
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        if (this.channel != null && channel != null && channel != this.channel) {
            throw new IllegalStateException();
        }
        this.channel = channel;
    }

    @XmlAttribute
    @JsonSerialize(using = NetToString.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    @XmlAttribute
    public String getImi() {
        return imi;
    }

    public void setImi(String value) {
        this.imi = value;
    }

    @XmlAttribute(required = true)
    public String getUrnRef() {
        if (urnRef == null && mediaObject != null) {
            return mediaObject.getUrn();
        }
        return urnRef;
    }

    public void setUrnRef(String value) {
        this.urnRef = value;
    }

    @XmlAttribute(required = true)
    public String getMidRef() {
        if (this.midRef == null && mediaObject != null) {
            return mediaObject.getMid();
        }
        return midRef;
    }

    public void setMidRef(String midRef) {
        this.midRef = midRef;
    }

    @Override
    @XmlTransient
    public Program getParent() {
        return mediaObject;
    }

    @Override
    public void setParent(Program mediaObject) {
        if (this.mediaObject != null) {
            this.mediaObject.removeScheduleEvent(this);
        }
        this.mediaObject = mediaObject;
        if (mediaObject != null) {
            mediaObject.addScheduleEvent(this);
        }
    }

    @XmlTransient
    @Override
    public ScheduleEventIdentifier getId() {
        if (channel != null && start != null) {
            return createId();
        } else {
            // schedule event has no proper id (yet?)
            return  null;
        }
    }

    protected ScheduleEventIdentifier createId() {
        ScheduleEventIdentifier id = new ScheduleEventIdentifier(); // avoid @NonNull validation
        id.start = start;
        id.channel = channel;
        return id;
    }

    @XmlAttribute
    public ScheduleEventType getType() {
        return type;
    }

    public void setType(ScheduleEventType type) {
        this.type = type;
    }

    @XmlElement
    public String getPoProgID() {
        return getMidRef();
    }

    public void setPoProgID(String poProgID) {
        setMidRef(poProgID);
    }

    @XmlTransient
    public String getPoSeriesID() {
        return poSeriesID;
    }

    public void setPoSeriesID(String poSeriesID) {
        this.poSeriesID = poSeriesID;
    }

    @XmlElement(name = "poSeriesID")
    public String getPoSeriesIDLegacy() {
        return null;
    }

    public void setPoSeriesIDLegacy(String poSeriesID) {
        this.poSeriesID = poSeriesID;
    }

    public void clearMediaObject() {
        if (this.mediaObject != null) {
            this.mediaObject.removeScheduleEvent(this);
            this.mediaObject = null;
        }
    }

    public Lifestyle getPrimaryLifestyle() {
        return primaryLifestyle;
    }

    public void setPrimaryLifestyle(Lifestyle primaryLifestyle) {
        this.primaryLifestyle = primaryLifestyle;
    }

    public SecondaryLifestyle getSecondaryLifestyle() {
        return secondaryLifestyle;
    }

    public void setSecondaryLifestyle(SecondaryLifestyle secondaryLifestyle) {
        this.secondaryLifestyle = secondaryLifestyle;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ScheduleEvent");
        sb.append("{channel=").append(channel);
        if (start != null) {
            sb.append(", start=").append(start.atZone(Schedule.ZONE_ID).toLocalDateTime());
        }
        if (mediaObject != null) {
            sb.append(", mediaObject=").append(mediaObject.getMid() == null ? "(no mid)" : mediaObject.getMid()); // it seems that the title may be lazy, so just show mid of media object.
        }
        if (repeat != null && repeat.isRerun) {
            sb.append(", RERUN");
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Schedule events are sorted by start, if those are equal then on channel
     */
    @Override
    public int compareTo(ScheduleEvent o) {
        if (o == this) {
            return 0;
        }
        return createId().compareTo(o.createId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduleEvent that = (ScheduleEvent) o;

        if (getId() != null) {
            return Objects.equals(getId(), that.getId());
        } else {
            return hashCode() == that.hashCode();
        }
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        } else {
            return Objects.hash(channel, start, duration, net, imi);
        }
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof Program) {
            this.mediaObject = (Program) parent;
        }

        if (guideDay == null && start != null) {
            guideDay = guideLocalDate(start);
        }
    }

    /**
     * The titles associated with the schedule event.
     */
    @Override
    public SortedSet<ScheduleEventTitle> getTitles() {
        if (titles == null) {
            titles = new TreeSet<>();
        }
        return sorted(titles);

    }

    @Override
    public void setTitles(SortedSet<ScheduleEventTitle> titles) {
        this.titles = titles;
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, ScheduleEventTitle> getOwnedTitleCreator() {
        return (value, ownerType, textualType) -> new ScheduleEventTitle(ScheduleEvent.this, value, ownerType, textualType);
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, ScheduleEventDescription> getOwnedDescriptionCreator() {
        return (value, ownerType, textualType) -> new ScheduleEventDescription(ScheduleEvent.this, value, ownerType, textualType);
    }


    @Override
    public ScheduleEvent addTitle(ScheduleEventTitle title) {
        title.setParent(this);
        return TextualObject.super.addTitle(title);
    }

    @Override
    public SortedSet<ScheduleEventDescription> getDescriptions() {
        if (descriptions == null) {
            descriptions = new TreeSet<>();
        }
        return sorted(descriptions);
    }


    @Override
    public void setDescriptions(SortedSet<ScheduleEventDescription> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public ScheduleEvent addDescription(ScheduleEventDescription description) {
        description.setParent(this);
        return TextualObject.super.addDescription(description);
    }

    /**
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     */
    @Override
    public String getMainTitle() {
        return TextualObject.super.getMainTitle();
    }

    /**
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     */
    @Override
    public String getSubTitle() {
        return TextualObject.super.getSubTitle();
    }

    /**
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     */
    @Override
    public String getMainDescription() {
        return TextualObject.super.getMainDescription();
    }


    public Range<Instant> asRange() {
        return Range.closedOpen(start, start.plus(duration));
    }
    public void setRange(Range<Instant> range) {
        this.start = range.lowerEndpoint();
        this.duration = Duration.between(range.lowerEndpoint(), range.upperEndpoint());
    }

    public static class Builder {

        public Builder localStart(int year, int month, int day, int hour, int minute) {
            return localStart(LocalDateTime.of(year, month, day, hour, minute));
        }

        public Builder localStart(LocalDateTime localDateTime) {
            return start(localDateTime.atZone(Schedule.ZONE_ID).toInstant());
        }

        public Builder rerun(boolean b) {
            return repeat(b ? Repeat.rerun() : Repeat.original());
        }

        public Builder rerun(String text) {
            return repeat(Repeat.rerun(text));
        }

        public Builder mainTitle(String title) {
            return title(ScheduleEventTitle.builder().title(title).type(TextualType.MAIN).owner(OwnerType.BROADCASTER).build());
        }

        public Builder mainDescription(String description) {
            return description(ScheduleEventDescription.builder().title(description).type(TextualType.MAIN).owner(OwnerType.BROADCASTER).build());
        }

    }
}
