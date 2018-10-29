package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.*;

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
}
