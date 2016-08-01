package nl.vpro.domain.stats;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
public interface PlayEventReporterService {

    Long playTotal(String mid);

    List<CountResult> playHistory(String mid, StatsView view, LocalDate begin, LocalDate end);

    List<CountResult>  playTimeLine(String mid);

    List<CountResult> playsPerBroadcaster(StatsView view, Date timestamp);

    List<CountResult> mediaOverall(StatsView view, Date timestamp);

    List<CountResult> mediaForBroadcaster(String broadcasterId, StatsView view, Date timestamp);

}
