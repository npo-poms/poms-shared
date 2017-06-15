package nl.vpro.spring.web.controllers.extjs.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import nl.vpro.domain.media.MediaService;
import nl.vpro.domain.media.ScheduleEventService;


@Controller
public class GuideServiceController {
    @Autowired
    private ScheduleEventService scheduleEventService;

    @Autowired
    private MediaService mediaService;


/*
    @Autowired
    private Jaxb2Marshaller marshaller;

    @RequestMapping("/schedules.html")
    @ModelAttribute("schedules")
    public List<Schedule> schedules() {
        return scheduleService.findAllSchedules();
    }

    @RequestMapping("/schedule.html")
    @ModelAttribute("schedule")
    public Schedule schedule(@RequestParam("chn") String chn, @RequestParam("day") String day) throws Exception {
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(day);
        return scheduleService.findSchedule(Channel.valueOfLegacy(chn), date);
    }

    @RequestMapping("/program.html")
    @ModelAttribute("program")
    public MediaObject getProgram(@RequestParam("prgId") Long id) {
        return mediaService.getFullyInitializedProgram(id);
    }
*/
}
