package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Data
public class MediaUpdateTable {


    ScheduleUpdate schedule = null;

    List<ProgramUpdate> programs = new ArrayList<>();

    List<GroupUpdate> groups = new ArrayList<>();

    public void addGroups(Collection<GroupUpdate> values) {
        groups.addAll(values);
    }

    public void addPrograms(Collection<ProgramUpdate> values) {
        programs.addAll(values);
    }
}
