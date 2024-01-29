package nl.vpro.domain.api.media;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.ApiScheduleEvent;
import nl.vpro.domain.api.Result;

/**
 * Exists only because of https://jira.vpro.nl/browse/API-118
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "scheduleResult")
@XmlType(name = "scheduleResultType")
public class ScheduleResult extends Result<ApiScheduleEvent> {


    public ScheduleResult() {
    }

    public ScheduleResult(final Result<? extends ApiScheduleEvent> apiScheduleEventResult) {
        super(apiScheduleEventResult);

   }


}
