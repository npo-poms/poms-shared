package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.math.abstractalgebra.Streamable;

import com.google.common.collect.Iterators;

import nl.vpro.domain.media.MediaTable;
import nl.vpro.domain.media.support.OwnerType;

/**
 * This is the 'update' version of a link {@link MediaTable}.
 * <p>
 * Collects a number of {@link ProgramUpdate}s with a number of {@link GroupUpdate}s and a {@link ScheduleUpdate}.
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 * @see nl.vpro.domain.media.update
 * @see MediaTable
 */
@Data
@XmlRootElement
@XmlType(name = "mediaUpdateTableType",
    propOrder = {
        "programTable",
        "groupTable",
        "schedule"}
        )
@XmlAccessorType(XmlAccessType.NONE)
public class MediaUpdateTable implements Iterable<MediaUpdate<?>>, Streamable<MediaUpdate<?>> {

    @XmlElementWrapper(name = "programTable")
    @XmlElement(name = "program")
    List<@Valid ProgramUpdate> programTable = new ArrayList<>();

    @XmlElementWrapper(name = "groupTable")
    @XmlElement(name = "group")
    List<@Valid GroupUpdate> groupTable = new ArrayList<>();

    @XmlElement
    @Valid
    ScheduleUpdate schedule = null;

    public void addGroups(Collection<GroupUpdate> values) {
        groupTable.addAll(values);
    }

    public void addPrograms(Collection<ProgramUpdate> values) {
        programTable.addAll(values);
    }

    /**
     * @since 7.7
     */
    public MediaUpdateTable add(MediaUpdate<?> mo) {
        if (mo instanceof ProgramUpdate program) {
            getProgramTable().add(program);
        } else if (mo instanceof GroupUpdate group) {
            getGroupTable().add(group);
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }

    public void add(MediaUpdateTable table) {
        groupTable.addAll(table.getGroupTable());
        programTable.addAll(table.getProgramTable());
        if (schedule != null) {
            schedule.add(table.getSchedule());
        } else {
            schedule = table.getSchedule();
        }
    }

    public Optional<GroupUpdate> getGroup(String mid) {
        return getGroupTable().stream().filter((g) -> mid.equals(g.getMid())).findFirst();
    }


    public Optional<ProgramUpdate> getProgram(String mid) {
        return getProgramTable().stream().filter((p) -> mid.equals(p.getMid())).findFirst();
    }

    public Optional<ProgramUpdate> getProgramByCrid(String crid) {
        return getProgramTable().stream().filter(p -> p.getCrids().contains(crid)).findFirst();
    }


    public MediaTable fetch(OwnerType type) {
        MediaTable result = new MediaTable();
        for (ProgramUpdate update :  programTable) {
            result.addProgram(update.fetch(type));
        }
        for (GroupUpdate update :  groupTable) {
            result.addGroup(update.fetch(type));
        }
        if (schedule != null) {
            result.setSchedule(
                schedule.fetch(type)
            );
        }
        return result;
    }

    public int size() {
        return getProgramTable().size() + getGroupTable().size();
    }

    @Override
    public Stream<MediaUpdate<?>> stream() {
        return StreamSupport.stream(
            Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED), false);
    }

    @NonNull
   @Override
   public Iterator<MediaUpdate<?>> iterator() {
       return Iterators.concat(
           getGroupTable().listIterator(), // first the groups, because they probably are referenced by program, so if this is used for sending the complete table, this is handy
           getProgramTable().listIterator()
       );
   }
}
