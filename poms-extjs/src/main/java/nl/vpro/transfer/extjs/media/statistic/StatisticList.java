/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.statistic;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.statistic.media.StatisticsService;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "statistics")
public class StatisticList extends TransferList<StatisticView> {

    public StatisticList() {
    }

    public static StatisticList create(StatisticsService service) {
        StatisticList simpleList = new StatisticList();
        simpleList.success = true;
        simpleList.add(StatisticView.loadFrom(service));
        return simpleList;
    }
}