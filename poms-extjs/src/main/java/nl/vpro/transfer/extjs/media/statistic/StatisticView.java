/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.statistic;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.statistic.media.StatisticsService;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "webUpdates",
        "misUpdates",
        "ceresUpdates",
        "radioboxUpdates",
        "whatsOnUpdates",
        "broadcasterUpdates"
        })
public class StatisticView {

    @XmlElement
    private String webUpdates;

    @XmlElement
    private String misUpdates;

    @XmlElement
    private String ceresUpdates;

    @XmlElement
    private String radioboxUpdates;

    @XmlElement
    private String immixUpdates;

    @XmlElement
    private String whatsOnUpdates;

    @XmlElement
    private String[][] broadcasterUpdates;

    @XmlElement
    private String[][] topUsers;

    private StatisticView() {
    }

    public static StatisticView loadFrom(StatisticsService service) {
        StatisticView simple = new StatisticView();
        simple.webUpdates = "" + service.webUpdatesLastHour() + '/' + service.webUpdatesLastDay();
        simple.misUpdates = "" + service.updatesLastHour(OwnerType.MIS) + '/' + service.updatesLastDay(OwnerType.MIS);
        simple.ceresUpdates = "" + service.updatesLastHour(OwnerType.CERES) + '/' + service.updatesLastDay(OwnerType.CERES);
        simple.radioboxUpdates = "" + service.updatesLastHour(OwnerType.RADIOBOX) + '/' + service.updatesLastDay(OwnerType.RADIOBOX);
        simple.immixUpdates = "" + service.updatesLastHour(OwnerType.IMMIX) + '/' + service.updatesLastDay(OwnerType.IMMIX);
        simple.whatsOnUpdates = "" + service.updatesLastHour(OwnerType.WHATS_ON) + '/' + service.updatesLastDay(OwnerType.WHATS_ON);
        simple.broadcasterUpdates = service.broadcasterImports();
        simple.topUsers = service.topUsers();
        return simple;
    }

    public String getWebUpdates() {
        return webUpdates;
    }

    public String getMisUpdates() {
        return misUpdates;
    }

    public String getCeresUpdates() {
        return ceresUpdates;
    }

    public String getRadioboxUpdates() {
        return radioboxUpdates;
    }

    public void setRadioboxUpdates(String radioboxUpdates) {
        this.radioboxUpdates = radioboxUpdates;
    }

    public String getImmixUpdates() {
        return immixUpdates;
    }

    public void setImmixUpdates(String immixUpdates) {
        this.immixUpdates = immixUpdates;
    }

    public String getWhatsOnUpdates() {
        return ceresUpdates;
    }

    public String[][] getBroadcasters() {
        return broadcasterUpdates;
    }

    public String[][] getTopUsers() {
        return topUsers;
    }
}
