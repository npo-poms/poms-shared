package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.UnmodifiableIterator;

import nl.vpro.util.DateUtils;

import static nl.vpro.domain.media.MediaObjects.deepCopy;
import static nl.vpro.util.DateUtils.toDate;


@XmlRootElement(name = "schedule")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleType", propOrder = {
    "scheduleEvents"
})
@Slf4j
public class Schedule implements Serializable, Iterable<ScheduleEvent>, Predicate<ScheduleEvent> {

    private static long serialVersionUID = 0L;


    public static ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");
    public static LocalTime START_OF_SCHEDULE = LocalTime.of(6, 0);

    public static LocalDate guideDay(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime dateTime = instant.atZone(Schedule.ZONE_ID);

        if (dateTime.toLocalTime().isBefore(Schedule.START_OF_SCHEDULE.minus(2, ChronoUnit.MINUTES))) {
            dateTime = dateTime.minusDays(1);
        }

        return dateTime.toLocalDate();
    }
    public static LocalDate guideDay() {
        return guideDay(Instant.now());
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
    protected Date start;

    @XmlTransient // See property
    protected Date stop;

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

    public Schedule(Date start, Date stop) {
        this((Channel)null, start, stop, null);
    }


    public Schedule(Instant  start, Instant stop) {
        this((Channel) null, toDate(start), toDate(stop), null);
    }

    public Schedule(Channel channel, Date start) {
        this(channel, start, start);
    }

    public Schedule(Channel channel, Instant start) {
        this(channel, toDate(start), toDate(start));
    }


    public Schedule(Channel channel, LocalDate date) {
        this(channel, date, Collections.emptyList());
    }


    public Schedule(Net net, Date start) {
        this(net, start, start);
    }

    public Schedule(Channel channel, LocalDate start, Collection<ScheduleEvent> scheduleEvents) {
        this(channel,
            Date.from(start.atStartOfDay(Schedule.ZONE_ID).toInstant()),
            Date.from(start.plusDays(1).atStartOfDay(Schedule.ZONE_ID).toInstant()),
            scheduleEvents);
    }

    public Schedule(Channel channel, Date start, Collection<ScheduleEvent> scheduleEvents) {
        this(channel, start, start, scheduleEvents);
    }


    public Schedule(Channel channel, Instant start, Collection<ScheduleEvent> scheduleEvents) {
        this(channel, start, start, scheduleEvents);
    }

    public Schedule(Net net, Date start, Collection<ScheduleEvent> scheduleEvents) {
        this(net, start, start, scheduleEvents);
    }

    public Schedule(Channel channel, Date start, Date stop) {
        this(channel, start, stop, null);
    }


    public Schedule(Net net, Date start, Date stop) {
        this(net, start, stop, null);
    }

    public Schedule(Channel channel, Date start, Date stop, Collection<ScheduleEvent> scheduleEvents) {
        this.channel = channel;
        this.start = start;
        this.stop = stop;
        if (scheduleEvents != null && scheduleEvents.size() > 0) {
            this.scheduleEvents = new TreeSet<>(scheduleEvents);
        }
    }

    public Schedule(Channel channel, Instant start, Instant stop, Collection<ScheduleEvent> scheduleEvents) {
        this(channel, toDate(start), toDate(stop), scheduleEvents);
    }

    public Schedule(Channel channel, Instant start, Instant stop) {
        this(channel, toDate(start), toDate(stop), null);
    }

    public Schedule(Net net, Date start, Date stop, Collection<ScheduleEvent> scheduleEvents) {
        this.net = net;
        this.start = start;
        this.stop = stop;
        if (scheduleEvents != null && scheduleEvents.size() > 0) {
            this.scheduleEvents = new TreeSet<>(scheduleEvents);
        }
    }

    public Schedule(Net net, Instant start, Instant stop) {
        this(net, toDate(start), toDate(stop), null);
    }

    public Schedule(Net net, Instant start, Instant stop, Collection<ScheduleEvent> scheduleEvents) {
        this(net, toDate(start), toDate(stop), scheduleEvents);
    }

    @lombok.Builder
    private Schedule(Net net, Instant start, Instant stop, Collection<ScheduleEvent> scheduleEvents, Boolean filtered) {
        this.net = net;
        this.start = DateUtils.toDate(start);
        this.stop = DateUtils.toDate(stop);
        if (scheduleEvents != null && scheduleEvents.size() > 0) {
            this.scheduleEvents = new TreeSet<>(scheduleEvents);
        }
        this.filtered = filtered == null ? false : filtered;

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

    public ScheduleEvent findScheduleEvent(final ScheduleEvent event) {
        for (ScheduleEvent e : scheduleEvents) {
            if (e.equals(event)) {
                return e;
            }
        }
        return null;
    }

    public void addScheduleEventsFromMedia(Collection<? extends MediaObject> mediaObjects) {
        if (scheduleEvents == null) {
            scheduleEvents = new TreeSet<>();
        }
        for (MediaObject mediaObject : mediaObjects) {
            scheduleEvents.addAll(mediaObject.getScheduleEvents());
        }
    }

    public void addScheduleEventsFromMedia(MediaObject mediaObject) {
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


    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) throws ClassNotFoundException, IOException {
        if (parent instanceof MediaTable) {
            List<Program> programs = ((MediaTable) parent).getProgramTable();
            if (scheduleEvents != null) {
                for (ScheduleEvent scheduleEvent : scheduleEvents) {
                    Program clone = null;

                    for (Program program : programs) {
                        if (program.getCrids().size() > 0
                            && StringUtils.isNotEmpty(scheduleEvent.getUrnRef())
                            && program.getCrids().get(0).equals(scheduleEvent.getUrnRef())) {

                            // MIS TVAnytime stores poProgId's under events. Therefore MIS deliveries may contain two or more
                            // ScheduleEvents referencing the same program on crid with different poProgId's.
                            // See test case.

                            String scheduleEventPoProgID = scheduleEvent.getPoProgID();
                            log.debug("No poprogid for {}", scheduleEvent);
                            if (program.getMid() == null || (scheduleEventPoProgID != null && scheduleEventPoProgID.equals(program.getMid()))) {
                                program.setMid(scheduleEventPoProgID);
                                scheduleEvent.setParent(program);
                            } else {
                                log.debug("Cloning a MIS duplicate");
                                // Create a clone for the second poProgId and it's event
                                clone = cloneMisDuplicate(program);
                                if (scheduleEventPoProgID != null) {
                                    /* Reset MID to null first, then set it to the poProgID from the Schedule event; otherwise an
                                     IllegalArgumentException will be thrown setting the MID to another value.
                                    */
                                    clone.setMid(null);
                                    clone.setMid(scheduleEventPoProgID);
                                }
                                scheduleEvent.setParent(clone);
                            }
                            break;
                        }
                    }

                    if (clone != null) {
                        programs.add(clone);
                    }
                }
            }
        }
    }

    private Program cloneMisDuplicate(Program program) throws ClassNotFoundException, IOException {
        Program clone = deepCopy(program);

        // Prevent constraint violation on duplicate crids
        for (String crid : clone.getCrids()) {
            clone.removeCrid(crid);
        }

        // Do not copy events
        List<ScheduleEvent> events = new ArrayList<>(clone.getScheduleEvents());
        for (ScheduleEvent event : events) {
            event.clearMediaObject();
        }

        return clone;
    }

    public Date getStart() {
        return start;
    }
    public Instant getStartInstant() {
        return toInstant(start);
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setGuideDate(LocalDate start) {
        this.start = start == null ? null : Date.from(start.atTime(START_OF_SCHEDULE).atZone(ZONE_ID).toInstant());
    }

    /* Need a getter with the above setter, otherwise Hibernate fails */
    public LocalDate getGuideDate() {
        return LocalDate.from(getStartInstant());
    }

    public void setStart(LocalDateTime start) {
        this.start = start == null ? null : Date.from(start.atZone(ZONE_ID).toInstant());
    }


    @XmlAttribute(name = "stop")
    public Date getStop() {
        if (filtered || scheduleEvents == null || scheduleEvents.size() == 0 || scheduleEvents.last().getStartInstant() == null) {
            return stop;
        }

        ScheduleEvent lastEvent = scheduleEvents.last();
        Instant collectionEnd = lastEvent.getStartInstant();
        if (lastEvent.getDuration() != null) {
            collectionEnd = lastEvent.getStartInstant().plus(lastEvent.getDuration());
        }

        return stop.getTime() >= collectionEnd.toEpochMilli() ? stop : Date.from(collectionEnd);

    }

    public void setStop(Date stop) {
        this.stop = stop;
    }

    public Instant getStopInstant() {
        return toInstant(getStop());
    }

    public void setStop(LocalDateTime stop) {
        this.stop = stop == null ? null : Date.from(stop.atZone(ZONE_ID).toInstant());
    }

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
        return (start == null || event.getStartInstant().compareTo(getStartInstant()) >= 0) &&
                (stop == null || event.getStartInstant().compareTo(getStopInstant()) <= 0);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Schedule");
        sb.append("{scheduleEvents=").append(scheduleEvents);
        sb.append(", channel=").append(channel);
        if (start != null) {
            sb.append(", start=").append(start);
        }
        if (stop != null) {
            sb.append(", stop=").append(stop);
        }
        sb.append(", releaseVersion=").append(releaseVersion);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Iterator<ScheduleEvent> iterator() {
        return getScheduleEvents().iterator();
    }


    private class ScheduleEventSet extends AbstractSet<ScheduleEvent> implements SortedSet<ScheduleEvent> {
        SortedSet<ScheduleEvent> events;

        public ScheduleEventSet(SortedSet<ScheduleEvent> events) {
            this.events = events;
        }

        @Override
        public Iterator<ScheduleEvent> iterator() {
            return new UnmodifiableIterator<ScheduleEvent>() {
                Iterator<ScheduleEvent> it = events.iterator();
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

        @Override
        public SortedSet<ScheduleEvent> subSet(ScheduleEvent fromElement, ScheduleEvent toElement) {
            return events.subSet(fromElement, toElement);
        }

        @Override
        public SortedSet<ScheduleEvent> headSet(ScheduleEvent toElement) {
            return events.headSet(toElement);
        }

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
