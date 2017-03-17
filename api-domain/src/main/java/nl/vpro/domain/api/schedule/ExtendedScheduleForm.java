package nl.vpro.domain.api.schedule;

import lombok.ToString;

import java.time.LocalDate;

import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Net;
import nl.vpro.domain.media.ScheduleEvent;
import nl.vpro.domain.media.search.DateRange;
import nl.vpro.domain.media.search.ScheduleForm;
import nl.vpro.domain.media.search.SchedulePager;
import nl.vpro.domain.user.Broadcaster;

/**
 * The API uses ES for schedules, so the form needs to be a bit smarter.
 * @author Michiel Meeuwissen
 * @since 3.6
 */
@ToString(callSuper = true)
class ExtendedScheduleForm extends ScheduleForm {

    private String broadcaster;

    private String net;

    private MediaType mediaType;

    private LocalDate guideDay;

    public ExtendedScheduleForm(SchedulePager pager, DateRange dateRange) {
        super(pager, dateRange);
    }

    public ExtendedScheduleForm(SchedulePager pager, LocalDate guideDay) {
        super(pager, null);
        this.guideDay = guideDay;
    }

    @Override
    public boolean test(ScheduleEvent e) {
        return super.test(e)
                && (broadcaster == null || e.getMediaObject().getBroadcasters().contains(new Broadcaster(broadcaster)))
                && (guideDay == null || (e.getGuideDate() != null && e.getGuideDate().equals(guideDay)))
                && (net == null || (e.getNet() != null && e.getNet().equals(new Net(net))))
                && (mediaType == null || (e.getMediaObject().getMediaType() == mediaType));
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public LocalDate getGuideDay() {
        return guideDay;
    }

    public void setGuideDay(LocalDate guideDay) {
        this.guideDay = guideDay;
    }
}
