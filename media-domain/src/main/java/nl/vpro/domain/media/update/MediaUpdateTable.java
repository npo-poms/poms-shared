package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public MediaTable fetch(OwnerType type) {
        MediaTable result = new MediaTable();
        for (ProgramUpdate update :  programTable) {
            result.addProgram(update.fetch(type));
        }
        for (GroupUpdate update :  groupTable) {
            result.addGroup(update.fetch(type));
        }
        if (schedule != null) {
            result.setSchedule(schedule.fetch(type));
        }
        return result;
    }
}
