package nl.vpro.domain.media;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Child;
import nl.vpro.domain.media.bind.NetToString;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DateToDuration;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */
public abstract class AbstractScheduleEvent implements Child<MediaObject> {

    AbstractScheduleEvent() {

    }

    AbstractScheduleEvent(Channel channel, Net net, LocalDate guideDay, Date start, Date duration, MediaObject media) {
        this.channel = channel;
        this.net = net;
        this.guideDay = guideDay;
        this.start = start;
        this.duration = duration;
        setParent(media);
    }


    @Id
    @Enumerated(EnumType.STRING)
    @NotNull
    protected Channel channel;

    @ManyToOne
    @Valid
    protected Net net;

    @Id
    @NotNull
    protected Date start;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    protected LocalDate guideDay;

    @Embedded
    protected Repeat repeat;

    protected String memberOf;

    @OneToOne(orphanRemoval = true)
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    protected AVAttributes avAttributes;

    protected String textSubtitles;

    @Column(name = "start_offset")
    @Temporal(TemporalType.TIME)
    protected Date offset;

    @Temporal(TemporalType.TIME)
    protected Date duration;

    protected String imi;

    @Transient
    protected String urnRef;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    protected MediaObject mediaObject;

    @Enumerated(EnumType.STRING)
    protected ScheduleEventType type;

    @Embedded
    @Column(name = "primary")
    protected Lifestyle primaryLifestyle;

    @Embedded
    @Column(name = "secondary")
    protected SecondaryLifestyle secondaryLifestyle;

    @Transient
    protected String midRef;

    protected String poSeriesID;

    @XmlElement
    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat value) {
        this.repeat = value;
    }

    @XmlElement
    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String value) {
        this.memberOf = value;
    }

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
    @XmlSchemaType(name = "date")
    public LocalDate getGuideDay() {
        return guideDay;
    }

    public void setGuideDay(LocalDate guideDay) {
        this.guideDay = guideDay;
    }

    @XmlElement
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        // if (this.start != null) throw new IllegalStateException(); Used in test cases.
        this.start = start;
    }

    @XmlTransient
    public Date getRealStart() {
        if (start == null) {
            return null;
        }

        if (offset == null) {
            return start;
        }

        return new Date(start.getTime() + offset.getTime());
    }

    @XmlJavaTypeAdapter(DateToDuration.class)
    @Temporal(TemporalType.TIME)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)

    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerDate.class)
    public Date getOffset() {
        return offset;
    }

    public void setOffset(Date offset) {
        this.offset = offset;
    }

    @XmlJavaTypeAdapter(DateToDuration.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerDate.class)
    public Date getDuration() {
        return duration;
    }

    public void setDuration(Date value) {
        this.duration = value;
    }

    public void setImi(String value) {
        this.imi = value;
    }

    @XmlAttribute
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        if (this.channel != null) {
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

    @XmlTransient
    @Override
    public MediaObject getParent() {
        return mediaObject;
    }

    @Override
    public void setParent(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }


    @XmlAttribute
    public ScheduleEventType getType() {
        return type;
    }

    public void setType(ScheduleEventType type) {
        this.type = type;
    }

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
        sb.append(", start=").append(start);
        sb.append(", mediaObject=").append(mediaObject);
        sb.append('}');
        return sb.toString();
    }


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof MediaObject) {
            this.mediaObject = (MediaObject) parent;
        }

        if (guideDay == null && start != null) {
            guideDay = guideLocalDate(start);
        }
    }


    protected static Date guideDay(Date start) {
        return Date.from(guideLocalDate(start).atStartOfDay(Schedule.ZONE_ID).toInstant());
    }

    private static LocalDate guideLocalDate(Date start) {
        ZonedDateTime dateTime = start.toInstant().atZone(Schedule.ZONE_ID);

        if (dateTime.toLocalTime().isBefore(Schedule.START_OF_SCHEDULE.minus(2, ChronoUnit.MINUTES))) {
            dateTime = dateTime.minusDays(1);
        }

        return dateTime.toLocalDate();
    }


}
