package nl.vpro.domain.api.schedule;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collection;

import nl.vpro.domain.media.DescendantRef;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.domain.media.search.*;
import nl.vpro.domain.user.Broadcaster;

/**
 * The API uses ES for schedules, so the form needs to be a bit smarter.
 * @author Michiel Meeuwissen
 * @since 3.6
 */
@ToString(callSuper = true)
@Data
@EqualsAndHashCode(callSuper = true)
class ExtendedScheduleForm extends ScheduleForm {

    private String broadcaster;

    private String net;

    private MediaType mediaType;

    /**
     * Use {@link #getGuideDayRange()}
     */
    @Deprecated
    private LocalDate guideDay;

    private Collection<String> descendantOf;


    public ExtendedScheduleForm(SchedulePager pager, InstantRange dateRange) {
        super(pager, dateRange, null, null);
    }

    public ExtendedScheduleForm(SchedulePager pager, LocalDate guideDay) {
        super(pager, null, new LocalDateRange(guideDay, guideDay), null);
    }

    @Override
    public boolean test(ScheduleEvent e) {
        return super.test(e)
            && (broadcaster == null || e.getParent().getBroadcasters().contains(new Broadcaster(broadcaster)))
            && (guideDay == null || (e.getGuideDate() != null && e.getGuideDate().equals(guideDay)))
            && (net == null || (e.getNet() != null && e.getNet().equals(new Net(net))))
            && (mediaType == null || (e.getParent().getMediaType() == mediaType))
            && (descendantOf == null || descendantOf.isEmpty() ||
            e.getParent().getDescendantOf().stream().map(DescendantRef::getMidRef).anyMatch(descendantOf::contains));

    }

}
