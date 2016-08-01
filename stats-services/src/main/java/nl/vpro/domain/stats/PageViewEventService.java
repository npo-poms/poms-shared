package nl.vpro.domain.stats;

import java.io.IOException;

/**
 * @author Michiel Meeuwissen
 * @since 3.1
 */
public interface PageViewEventService {

    void addEvent(PageViewEvent event) throws IOException;

    void updateEvent(TimeUpdate update);
}
