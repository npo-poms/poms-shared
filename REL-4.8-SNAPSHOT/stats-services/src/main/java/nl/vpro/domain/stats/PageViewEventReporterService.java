package nl.vpro.domain.stats;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
public interface PageViewEventReporterService {


    List<CountResult> pageStats(String url, StatsView view, LocalDate begin, LocalDate end);

    List<CountResult> portalStats(String url, StatsView view, LocalDate begin, LocalDate end);


}
