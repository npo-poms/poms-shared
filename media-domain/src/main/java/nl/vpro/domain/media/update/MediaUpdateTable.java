package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.MediaTable;
import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
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
public class MediaUpdateTable {

    @XmlElementWrapper(name = "programTable")
    @XmlElement(name = "program")
    List<ProgramUpdate> programTable = new ArrayList<>();

    @XmlElementWrapper(name = "groupTable")
    @XmlElement(name = "group")
    List<GroupUpdate> groupTable = new ArrayList<>();

    @XmlElement
    ScheduleUpdate schedule = null;

    public void addGroups(Collection<GroupUpdate> values) {
        groupTable.addAll(values);
    }

    public void addPrograms(Collection<ProgramUpdate> values) {
        programTable.addAll(values);
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
}
