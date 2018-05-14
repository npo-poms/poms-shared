package nl.vpro.domain.media.update;

import lombok.Data;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Data
public class MediaUpdateTable {


    ScheduleUpdate schedule;

    List<ProgramUpdate> programs;

    List<ProgramUpdate> groups;
}
