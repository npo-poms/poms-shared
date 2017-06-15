package nl.vpro.spring.web.controllers.extjs.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.vpro.statistic.media.StatisticsService;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.media.statistic.StatisticList;

@Controller
@RequestMapping(value = "/stats")
public class StatisticController {

    @Autowired
    private StatisticsService statsService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> stats() {
        return StatisticList.create(statsService);
    }

}