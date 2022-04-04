package nl.vpro.domain.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;
import com.google.common.collect.UnmodifiableIterator;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.util.Ranges;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.util.DateUtils.toDate;


/**
 * Representation of a time {@link #asRange()} containing a collections of {@link ScheduleEvent}s, for a certain {@link Channel}
 */

@XmlRootElement(name = "schedule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleType", propOrder = {
    "scheduleEvents"
})
@Slf4j
public class Schedule implements Serializable, Iterable<ScheduleEvent>, Predicate<ScheduleEvent> {

    private static final long serialVersionUID = 0L;

    /**
     * The time zone most relevant for NPO, i.e. the timezone in the Netherlands.
     */
    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");
    public static final LocalTime START_OF_SCHEDULE = LocalTime.of(6, 0);

    public static LocalDate guideDay(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime dateTime = instant.atZone(Schedule.ZONE_ID);

        if (localTimeBelongsToPreviousDay(dateTime.toLocalTime())) {
            dateTime = dateTime.minusDays(1);
        }

        return dateTime.toLocalDate();
    }

    /**
     * @since 5.11
     */
    public static LocalDate guideDay(LocalDateTime datetime) {
        if (datetime == null) {
            return null;
        }
        LocalDate localDate = datetime.toLocalDate();
        if (localTimeBelongsToPreviousDay(datetime.toLocalTime())) {
            localDate = localDate.minusDays(1);
        }
        return localDate;
    }
     /**
     * @since 5.11
     */
    public static boolean localTimeBelongsToPreviousDay(@NonNull LocalTime localTime) {
        return localTime.isBefore(Schedule.START_OF_SCHEDULE.minus(2, ChronoUnit.MINUTES));
    }

    public static LocalDate guideDay() {
        return guideDay(instant());
    }

    public static Instant toInstant(LocalDateTime time) {
        return time.atZone(ZONE_ID).toInstant();
    }


    protected static Instant toInstant(Date time) {
        return DateUtils.toInstant(time);
    }


    @XmlTransient // See property
    protected SortedSet<ScheduleEvent> scheduleEvents;

    @XmlAttribute
    protected Channel channel;

    @XmlAttribute
    protected Net net;

    @XmlAttribute(name = "start")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant start;

    @XmlTransient // See property
    protected Instant stop;

    @XmlAttribute
    protected Integer releaseVersion;

    @XmlTransient
    protected boolean filtered = false;

    @XmlAttribute
    @Getter
    @Setter
    protected Boolean reruns;


    public Schedule() {
    }

    @Deprecated
    public Schedule(Date start, Date stop) {
        this((Channel)null, start, stop, null);
    }

    public Schedule(Instant  start, Instant stop) {
        this((Channel) null, start, stop, null);
    }

    @Deprecated
    public Schedule(Channel channel, Date start) {
        this(channel, start, start);
    }

    public Schedule(Channel channel, Instant start) {
        this(channel, toDate(start), toDate(start));
    }


    public Schedule(Channel channel, LocalDate date) {
        this(channel, date, Collections.emptyList());
    }


    @Deprecated
    public Schedule(Net net, Date start) {
        this(net, start, start);
    }

    public Schedule(Channel channel, LocalDate start, Collection<ScheduleEvent> scheduleEvents) {
        this(channel,
            Date.from(start.atStartOfDay(Schedule.ZONE_ID).toInstant()),
            Date.from(start.plusDays(1).atStartOfDay(Schedule.ZONE_ID).toInstant()),
            scheduleEvents);
    }

    @Deprecated
    public Schedule(Channel channel, Date start, Collection<ScheduleEvent> scheduleEvents) {
        this(channel, start, start, scheduleEvents);
    }


    public Schedule(Channel channel, Instant start, Collection<ScheduleEvent> scheduleEvents) {
        this(channel, start, start, scheduleEvents);
    }

    @Deprecated
    public Schedule(Net net, Date start, Collection<ScheduleEvent> scheduleEvents) {
        this(net, start, start, scheduleEvents);
    }

    @Deprecated
    public Schedule(Channel channel, Date start, Date stop) {
        this(channel, start, stop, null);
    }


    @Deprecated
    public Schedule(Net net, Date start, Date stop) {
        this(net, start, stop, null);
    }

    @Deprecated
    public Schedule(Channel channel, Date start, Date stop, Collection<ScheduleEvent> scheduleEvents) {
        this(channel, DateUtils.toInstant(start), DateUtils.toInstant(stop), scheduleEvents);
    }

    public Schedule(Channel channel, Instant start, Instant stop, Collection<ScheduleEvent> scheduleEvents) {
        this.channel = channel;
        this.start = start;
        this.stop = stop;
        if (scheduleEvents != null && scheduleEvents.size() > 0) {
            this.scheduleEvents = new TreeSet<>(scheduleEvents);
        }
    }

    public Schedule(Channel channel, Instant start, Instant stop) {
        this(channel, start, stop, null);
    }

    @Deprecated
    public Schedule(Net net, Date start, Date stop, Collection<ScheduleEvent> scheduleEvents) {
        this(net, DateUtils.toInstant(start), DateUtils.toInstant(stop), scheduleEvents);
    }

    public Schedule(Net net, Instant start, Instant stop) {
        this(net, start, stop, null);
    }

    public Schedule(Net net, Instant start, Instant stop, Collection<ScheduleEvent> scheduleEvents) {
        this.net = net;
        this.start = start;
        this.stop = stop;
        if (scheduleEvents != null && scheduleEvents.size() > 0) {
            this.scheduleEvents = new TreeSet<>(scheduleEvents);
            for (ScheduleEvent e : this.scheduleEvents) {
                if (e.getChannel() == null) {
                    e.setChannel(channel);
                }
                if (e.getNet() == null) {
                    e.setNet(net);
                }
            }
        }
    }

    @lombok.Builder(builderClassName = "Builder")
    private Schedule(
        Channel channel,
        Net net,
        Instant start,
        Instant stop,
        LocalDateTime localStart,
        LocalDateTime localStop,
        LocalDate startDay,
        LocalDate stopDay,
        @Singular
        Collection<ScheduleEvent> scheduleEvents,
        Boolean filtered) {
        this.net = net;
        this.channel = channel;
        this.start = of(start, localStart, startDay);
        this.stop = of(stop, localStop, stopDay);
        if (scheduleEvents != null && scheduleEvents.size() > 0) {
            this.scheduleEvents = new TreeSet<>(scheduleEvents);
            for (ScheduleEvent e : this.scheduleEvents) {
                if (e.getChannel() == null) {
                    e.setChannel(channel);
                }
                if (e.getNet() == null) {
                    e.setNet(net);
                }
            }
        }
        this.filtered = filtered == null ? false : filtered;

    }


    public static Instant of(Instant instant, LocalDateTime localDateTime, LocalDate localDate) {
        if (instant != null) {
            assert  localDateTime == null;
            assert  localDate == null;
            return instant;
        }
        if (localDateTime != null) {
            assert  localDate == null;
            return localDateTime.atZone(ZONE_ID).toInstant();
        }
        if (localDate != null) {
            return localDate.atTime(START_OF_SCHEDULE).atZone(ZONE_ID).toInstant();
        }
        return null;
    }

    @XmlElement(name = "scheduleEvent")
    public SortedSet<ScheduleEvent> getScheduleEvents() {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        if (filtered) {
            return new ScheduleEventSet(scheduleEvents);
        } else {
            return scheduleEvents;
        }
    }

    public void addScheduleEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        scheduleEvents.add(scheduleEvent);
    }

    public boolean removeScheduleEvent(ScheduleEvent scheduleEvent) {
        if (scheduleEvents == null) {
            return false;
        }
        return scheduleEvents.remove(scheduleEvent);
    }

    /**
     * Finds in the current schedule the excact event (by database id)
     */
    public Optional<ScheduleEvent> findScheduleEvent(final ScheduleEvent event) {
        for (ScheduleEvent e : scheduleEvents) {
            if (e.equals(event)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }



    public void addScheduleEventsFromMedia(Collection<? extends Program> mediaObjects) {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        for (Program mediaObject : mediaObjects) {
            scheduleEvents.addAll(mediaObject.getScheduleEvents());
        }
    }


    public void addScheduleEventsFromMedia(Program... mediaObjects) {
        addScheduleEventsFromMedia(Arrays.asList(mediaObjects));
    }

    public void addScheduleEventsFromMedia(Program mediaObject) {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        scheduleEvents.addAll(mediaObject.getScheduleEvents());
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel value) {
        this.channel = value;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public Integer getReleaseVersion() {
        return releaseVersion;
    }



    public Instant getStart() {
        return start;
    }

    public void setStart(Instant start) {
        this.start = start;
    }

    public void setGuideDate(LocalDate start) {
        this.start = start == null ? null : start.atTime(START_OF_SCHEDULE).atZone(ZONE_ID).toInstant();
    }

    /* Need a getter with the above setter, otherwise Hibernate fails */
    public LocalDate getGuideDate() {
        return LocalDate.from(getStart());
    }

    public void setStart(LocalDateTime start) {
        this.start = start == null ? null : start.atZone(ZONE_ID).toInstant();
    }


    @XmlAttribute(name = "stop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getStop() {
        if (filtered || scheduleEvents == null || scheduleEvents.size() == 0 || scheduleEvents.last().getStartInstant() == null) {
            return stop;
        }

        ScheduleEvent lastEvent = scheduleEvents.last();
        Instant collectionEnd = lastEvent.getStartInstant();
        if (lastEvent.getDuration() != null) {
            collectionEnd = lastEvent.getStartInstant().plus(lastEvent.getDuration());
        }

        return stop == null || stop.isAfter(collectionEnd) ? stop : collectionEnd;

    }

    public void setStop(Instant stop) {
        this.stop = stop;
    }

    @XmlTransient
    public void setStop(LocalDateTime stop) {
        this.stop = stop == null ? null : stop.atZone(ZONE_ID).toInstant();
    }

    /**
     * If the schedule is 'filtered' then any scheduleEvents not actually in this schedule (as specified by fields like {@link #start}, {@link #stop}, {@link #channel}, {@link #net} and {@link #reruns} are not
     * returned in {@link #getScheduleEvents()}.
     */
    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    @Override
    public boolean test(ScheduleEvent event) {
        if (channel != null) {
            if (! Objects.equals(event.getChannel(), channel)) {
                return false;
            }
        }
        if (net != null) {
            if (! Objects.equals(event.getNet(), net)) {
                return false;
            }
        }
        if (reruns != null) {
            if (!reruns) {
                if (ScheduleEvents.isRerun(event)) {
                    return false;
                }
            } else {
                if (ScheduleEvents.isOriginal(event)) {
                    return false;
                }
            }
        }
        return inTimeRange(event);
    }

    private boolean inTimeRange(ScheduleEvent event) {
        return (start == null || event.getStartInstant().compareTo(getStart()) >= 0) &&
                (stop == null || event.getStartInstant().compareTo(getStop()) <= 0);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Schedule");
        sb.append("{scheduleEvents=").append(scheduleEvents);
        sb.append(", channel=").append(channel);
        if (start != null) {
            sb.append(", start=").append(start.atZone(Schedule.ZONE_ID).toLocalDateTime());
        }
        if (stop != null) {
            sb.append(", stop=").append(stop.atZone(Schedule.ZONE_ID).toLocalDateTime());
        }
        sb.append(", releaseVersion=").append(releaseVersion);
        sb.append('}');
        return sb.toString();
    }

    @Override
    @NonNull
    public Iterator<ScheduleEvent> iterator() {
        return getScheduleEvents().iterator();
    }

    /**
     * Returns the with this Schedule associated {@link #getStart()} and {@link #getStop()} instances as a {@link Range} of {@link ZonedDateTime}'s.
     *
     * I.e. an interval that is closed at the start, and open at the end. For now everything is associated with the time zone {@link #ZONE_ID} (i.e. CEST), since we
     * haven't accounted an use case for something else yet, but otherwise we may imaging also the time zone to be a member of this schedule object.
     */
    public Range<LocalDateTime> asLocalRange() {
        return DateUtils.toLocalDateTimeRange(asRange(), Schedule.ZONE_ID);
    }

    public Range<Instant> asRange() {
        return Ranges.closedOpen(getStart(), getStop());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (filtered != schedule.filtered) return false;
        if (!scheduleEvents.equals(schedule.scheduleEvents)) return false;
        if (channel != schedule.channel) return false;
        if (net != null ? !net.equals(schedule.net) : schedule.net != null) return false;
        if (!start.equals(schedule.start)) return false;
        if (!stop.equals(schedule.stop)) return false;
        if (releaseVersion != null ? !releaseVersion.equals(schedule.releaseVersion) : schedule.releaseVersion != null)
            return false;
        return reruns != null ? reruns.equals(schedule.reruns) : schedule.reruns == null;
    }

    @Override
    public int hashCode() {
        int result = scheduleEvents.hashCode();
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        result = 31 * result + (net != null ? net.hashCode() : 0);
        result = 31 * result + start.hashCode();
        result = 31 * result + stop.hashCode();
        result = 31 * result + (releaseVersion != null ? releaseVersion.hashCode() : 0);
        result = 31 * result + (filtered ? 1 : 0);
        result = 31 * result + (reruns != null ? reruns.hashCode() : 0);
        return result;
    }

    private class ScheduleEventSet extends AbstractSet<ScheduleEvent> implements SortedSet<ScheduleEvent> {
        SortedSet<ScheduleEvent> events;

        public ScheduleEventSet(SortedSet<ScheduleEvent> events) {
            this.events = events;
        }

        @NonNull
        @Override
        public Iterator<ScheduleEvent> iterator() {
            return new UnmodifiableIterator<ScheduleEvent>() {
                final Iterator<ScheduleEvent> it = events.iterator();
                ScheduleEvent next = null;

                @Override
                public boolean hasNext() {
                    findNext();
                    return next != null;
                }

                @Override
                public ScheduleEvent next() {
                    findNext();
                    if (next == null) {
                        throw new NoSuchElementException();
                    }
                    ScheduleEvent result = next;
                    next = null;
                    return result;
                }

                private void findNext() {
                    if (next == null) {
                        ScheduleEvent result;
                        while (it.hasNext()) {
                            result = it.next();
                            if (test(result)) {
                                next = result;
                                break;
                            }
                        }
                    }
                }
            };
        }

        @Override
        public int size() {
            Iterator<ScheduleEvent> iterator = iterator();
            int size = 0;
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
            return size;
        }

        @Override
        public Comparator<? super ScheduleEvent> comparator() {
            return events.comparator();
        }

        @NonNull
        @Override
        public SortedSet<ScheduleEvent> subSet(ScheduleEvent fromElement, ScheduleEvent toElement) {
            return events.subSet(fromElement, toElement);
        }

        @NonNull
        @Override
        public SortedSet<ScheduleEvent> headSet(ScheduleEvent toElement) {
            return events.headSet(toElement);
        }

        @NonNull
        @Override
        public SortedSet<ScheduleEvent> tailSet(ScheduleEvent fromElement) {
            return events.tailSet(fromElement);
        }

        @Override
        public ScheduleEvent first() {
            return iterator().next();
        }

        @Override
        public ScheduleEvent last() {
            SortedSet<ScheduleEvent> sortedUnfiltered = events;
            while (true) {
                ScheduleEvent element = sortedUnfiltered.last();
                if (test(element)) {
                    return element;
                }
                sortedUnfiltered = sortedUnfiltered.headSet(element);
            }
        }
    }
}
