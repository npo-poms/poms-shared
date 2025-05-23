package nl.vpro.domain.media;

import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    AbstractScheduleEvent(@NonNull Channel channel, Net net, @NonNull LocalDate guideDay, @NonNull Date start, Date duration, MediaObject media) {
        this.channel = channel;
        this.net = net;
        this.guideDay = guideDay;
        this.start = start;
        this.duration = duration;
        setParent(media);
    }


    @Id
    @Enumerated(EnumType.STRING)
    @MonotonicNonNull
    protected Channel channel;

    @Setter
    @ManyToOne
    @Valid
    protected Net net;

    // if (this.start != null) throw new IllegalStateException(); Used in test cases.
    @Setter
    @Id
    @NotNull
    protected Date start;

    @Setter
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @NotNull
    protected LocalDate guideDay;

    @Setter
    @Embedded
    protected Repeat repeat;

    @Setter
    protected String memberOf;

    @Setter
    @OneToOne(orphanRemoval = true)
    @org.hibernate.annotations.Cascade({
        org.hibernate.annotations.CascadeType.ALL
    })
    protected AVAttributes avAttributes;

    @Setter
    protected String textSubtitles;

    @Setter
    @Column(name = "start_offset")
    @Temporal(TemporalType.TIME)
    protected Date offset;

    @Setter
    @Temporal(TemporalType.TIME)
    protected Date duration;

    @Setter
    protected String imi;

    @Setter
    @Transient
    protected String urnRef;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    protected MediaObject mediaObject;

    @Deprecated
    @Setter
    @Enumerated(EnumType.STRING)
    protected ScheduleEventType type;

    @Setter
    @Embedded
    @Column(name = "primary")
    protected Lifestyle primaryLifestyle;

    @Setter
    @Embedded
    @Column(name = "secondary")
    protected SecondaryLifestyle secondaryLifestyle;

    @Setter
    @Transient
    protected String midRef;

    @Setter
    protected String poSeriesID;

    @XmlElement
    public Repeat getRepeat() {
        return repeat;
    }

    @XmlElement
    public String getMemberOf() {
        return memberOf;
    }

    @XmlElement
    public AVAttributes getAvAttributes() {
        return avAttributes;
    }

    @XmlElement
    public String getTextSubtitles() {
        return textSubtitles;
    }

    @XmlElement
    @XmlSchemaType(name = "date")
    public @NonNull LocalDate getGuideDay() {
        return guideDay;
    }

    @XmlElement
    public @NonNull Date getStart() {
        return start;
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

    @XmlJavaTypeAdapter(DateToDuration.class)
    @XmlElement
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerDate.class)
    public Date getDuration() {
        return duration;
    }

    @XmlAttribute
    public @NonNull Channel getChannel() {
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

    @XmlAttribute(required = true)
    public String getMidRef() {
        if (this.midRef == null && mediaObject != null) {
            return mediaObject.getMid();
        }
        return midRef;
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
    @Deprecated
    public ScheduleEventType getType() {
        return type;
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

    public SecondaryLifestyle getSecondaryLifestyle() {
        return secondaryLifestyle;
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

        if (dateTime.toLocalTime().isBefore(Schedule.START_OF_SCHEDULE.minusMinutes(2))) {
            dateTime = dateTime.minusDays(1);
        }

        return dateTime.toLocalDate();
    }


}
