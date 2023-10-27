package nl.vpro.domain.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.validation.Valid;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.math.abstractalgebra.Streamable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static nl.vpro.domain.media.MediaObjects.deepCopy;


@XmlRootElement(name = "mediaInformation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaTableType",
         propOrder = {
             "programTable",
             "groupTable",
             "locationTable",
             "schedule"}
)
@lombok.Builder
@AllArgsConstructor
@Slf4j
public class MediaTable implements Iterable<MediaObject>, Serializable, Streamable<MediaObject> {

    @Serial
    private static final long serialVersionUID = 4054512453318247403L;

    public MediaTable() {
    }

    @XmlElementWrapper(name = "programTable")
    @XmlElement(name = "program")
    protected List<@Valid Program> programTable;

    @XmlElementWrapper(name = "groupTable")
    @XmlElement(name = "group")
    protected List<@Valid Group> groupTable;

    @XmlElement
    @Getter
    @Setter
    @Valid
    protected LocationTable locationTable;

    @XmlElement
    @Setter
    @Valid
    protected Schedule schedule;

    @XmlAttribute
    @Getter
    @Setter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publicationTime;

    @XmlAttribute
    @Getter
    @Setter
    protected String publisher;

    @XmlAttribute
    @Getter
    @Setter
    protected Short version;

    @XmlAttribute
    @Getter
    @Setter
    protected String source;

    /**
     * @since 5.9
     */
    public MediaTable add(MediaObject mo) {
        if (mo instanceof Program program) {
            return addProgram(program);
        } else if (mo instanceof Group group) {
            return addGroup(group);
        } else {
            log.warn("Could not add {}", mo);
            return this;
        }
    }

    public MediaTable addProgram(Program program) {
        if(programTable == null) {
            programTable = new ArrayList<>();
        }

        programTable.add(program);
        return this;
    }

    public MediaTable add(MediaTable mo) {
        for (Program p : mo.getProgramTable()) {
            addProgram(p);
        }
        for (Group g : mo.getGroupTable()) {
            addGroup(g);
        }
        return this;
    }

    /**
     * Searches the mediaobject with given mid in the table. This may return a {@link Program}, {@link Group}, or {@link Segment}
     */
    @SuppressWarnings("unchecked")
    public <T extends MediaObject> Optional<T> find(String mid) {
        for (MediaObject p : Iterables.concat(getProgramTable(), getGroupTable())) {
            if (Objects.equals(p.getMid(), mid)) {
                return Optional.of((T) p);
            }
            if (p instanceof Program) {
                for (Segment s : ((Program) p).getSegments()) {
                    if (Objects.equals(s.getMid(), mid)) {
                        return Optional.of((T) s);
                    }
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @since 5.11
     */
    @SuppressWarnings("unchecked")
    public <T extends MediaObject> Optional<T> findByCrid(String crid) {
        for (MediaObject p : Iterables.concat(getProgramTable(), getGroupTable())) {
            if (p.getCrids().contains(crid)) {
                return Optional.of((T) p);
            }
        }
        return Optional.empty();
    }

    /**
     * @since 5.34
     */
    public Optional<Group> getGroup(String mid) {
        return getGroupTable().stream()
            .filter(g -> mid.equals(g.getMid()))
            .findFirst();
    }

    /**
     * @since 5.34
     */
    public Optional<Program> getProgram(String mid) {
        return getProgramTable().stream()
            .filter(p -> mid.equals(p.getMid()))
            .findFirst();
    }

    /**
     * Returns the schedule associated with this table. If there is none, then it will be a schedule based on all {@link ScheduleEvent}s of all {@link #getProgramTable()}.
     */

    public Schedule getSchedule() {
        if (schedule == null) {
            Schedule s = new Schedule();
            for (Program program : getProgramTable()) {
                for (ScheduleEvent event : program.getScheduleEvents()) {
                    s.addScheduleEvent(event);
                }
            }
            return s;

        }
        return schedule;
    }

    /**
     * @since 5.9
     */
    public boolean contains(String mid) {
        return find(mid).isPresent();
    }

    public List<Program> getProgramTable() {
        if(programTable == null) {
            programTable = new ArrayList<>();
        }
        return programTable;
    }

    public void setProgramTable(List<Program> programTable) {
        this.programTable = programTable;
    }

    public MediaTable addGroup(Group group) {
        if(groupTable == null) {
            groupTable = new ArrayList<>();
        }

        groupTable.add(group);
        return this;
    }

    public List<Group> getGroupTable() {
        if(groupTable == null) {
            groupTable = new ArrayList<>();
        }
        return this.groupTable;
    }

    public void setGroupTable(List<Group> groupTable) {
        this.groupTable = groupTable;
    }

    @Override
    public String toString() {
        return "MediaTable " + getGroupTable().size() + " groups " + getProgramTable().size() + " program " + getSchedule();
    }

    @NonNull
    @Override
    public Iterator<MediaObject> iterator() {
        return Iterators.concat(
            getProgramTable().listIterator(),
            getGroupTable().listIterator()
        );
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        mergePrograms();
        linkSchedule();
    }

    private void mergePrograms() {
        // if you provide a media table xml with duplicate programs, (with same mid), then we simply take only the
        // merge duplicate programs
        Map<String, List<Program>> map = new HashMap<>();
        for (Program program : programTable) {
            if (program.getMid() != null) {
                List<Program> programs = map.computeIfAbsent(program.getMid(), k -> new ArrayList<>());
                programs.add(program);
            }
        }
        for (Map.Entry<String, List<Program>>  e : map.entrySet()) {
            if (e.getValue().size() > 1) {
                Program first = e.getValue().get(0);
                log.debug("Found duplicate program {}", first);
                for (int i = 1; i < e.getValue().size(); i++) {
                    Program another = e.getValue().get(i);
                    for (String c : another.getCrids()) {
                        if (! first.getCrids().contains(c)) {
                            first.getCrids().add(c);
                        }
                    }
                    another.setMid(null); // to break equalsOnMid
                    programTable.remove(another);
                    log.debug("Removing {}", another);
                }
            }
        }
    }

    private void linkSchedule() {
        List<Program> programs = programTable;
        if (schedule.scheduleEvents != null) {
            for (ScheduleEvent scheduleEvent : schedule.scheduleEvents) {
                Program clone = null;

                for (Program program : programs) {
                    if (!program.getCrids().isEmpty()
                        && StringUtils.isNotEmpty(scheduleEvent.getUrnRef())
                        && program.getCrids().contains(scheduleEvent.getUrnRef())) {

                        // MIS TVAnytime stores poProgId's under events. Therefore MIS deliveries may contain two or more
                        // ScheduleEvents referencing the same program on crid with different poProgId's.
                        // See test case.

                        String scheduleEventPoProgID = scheduleEvent.getPoProgID();
                        log.debug("No poprogid for {}", scheduleEvent);
                        if (scheduleEventPoProgID != null) {
                            if (program.getMid() == null || scheduleEventPoProgID.equals(program.getMid())) {
                                program.setMid(scheduleEventPoProgID);
                                scheduleEvent.setParent(program);
                            } else {
                                log.debug("Cloning a MIS duplicate");
                                // Create a clone for the second poProgId and its event
                                clone = cloneMisDuplicate(program);
                                /* Reset MID to null first, then set it to the poProgID from the Schedule event; otherwise an
                                     IllegalArgumentException will be thrown setting the MID to another value.
                                    */
                                clone.setMid(null);
                                clone.setMid(scheduleEventPoProgID);
                                scheduleEvent.setParent(clone);
                            }
                        } else {
                            scheduleEvent.setParent(program);
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


    private Program cloneMisDuplicate(Program program) {
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

    public int size() {
        return getProgramTable().size() + getGroupTable().size();
    }

    @Override
    public Stream<MediaObject> stream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED), false);
    }

}
