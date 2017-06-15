package nl.vpro.spring.web.controllers.extjs.media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.ScheduleEventService;
import nl.vpro.domain.media.search.DateRange;
import nl.vpro.domain.media.search.Pager;
import nl.vpro.domain.media.search.ScheduleForm;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.media.ChannelList;
import nl.vpro.transfer.extjs.media.NetList;
import nl.vpro.util.DateUtils;
import nl.vpro.web.media.ExtScheduleService;

@Controller
@RequestMapping(value = "/schedules")
public class ScheduleController {

    @Autowired
    private ExtScheduleService scheduleService;

    @Autowired
    private BroadcasterService broadcasterService;

    @Autowired
    private ScheduleEventService scheduleEventService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> schedules(
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "limit", required = false, defaultValue = "25") int limit,
            @RequestParam(value = "sort", required = false, defaultValue = "guideDay") String sort,
            @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
            @RequestParam(value = "channelDir", required = false, defaultValue = "asc") String channelDir,
            @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(value = "stopDate", required = false, defaultValue = "") String stopDate,
            @RequestParam(value = "channels", required = false, defaultValue = "") String channels,
            @RequestParam(value = "broadcasters", required = false, defaultValue = "") String broadcasters
    ) throws ParseException {

        Pager pager = new Pager(start, limit, sort, Pager.Direction.valueOf(dir.toUpperCase()));


        SimpleDateFormat dayFormat = new SimpleDateFormat("ddMMyyyyHH");
        Date star = StringUtils.isNotEmpty(startDate) ? dayFormat.parse(startDate + "06") : null;
        Date stop = StringUtils.isNotEmpty(stopDate) ? dayFormat.parse(stopDate + "06") : null;

        if (stop != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(stop);
            cal.add(Calendar.DATE, 1);
            stop = cal.getTime();
        }
        DateRange range = new DateRange(
            DateUtils.toInstant(star),
            DateUtils.toInstant(stop));

        ScheduleForm form = new ScheduleForm(pager, range);

        if (StringUtils.isNotEmpty(channels)) {
            form.setChannels(Channel.valuesOf(Arrays.asList(channels.split(","))));
        }

        return scheduleService.search(form);
    }


    @RequestMapping(value = "/{channel}/{date}", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> scheduleData(@PathVariable(value = "channel") String channel, @PathVariable(value = "date") String date) throws Exception {
        Date scheduleDate = new SimpleDateFormat("ddMMyyyy").parse(date);

        return scheduleService.getSchedule(Channel.valueOf(channel), scheduleDate);
    }


    @RequestMapping(value = "/channels", method = RequestMethod.GET)
    @ResponseBody
    public TransferList channels() {
        return ChannelList.create(scheduleEventService.listChannelsInUse());

    }

    @RequestMapping(value = "/nets", method = RequestMethod.GET)
    @ResponseBody
    public TransferList nets() {
        return NetList.create(scheduleEventService.listNets());
    }
}
