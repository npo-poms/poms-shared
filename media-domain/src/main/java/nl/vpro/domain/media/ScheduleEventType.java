/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 * Creation date 3 nov 2008.
 */
package nl.vpro.domain.media;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @author roekoe
 * @deprecated Last 'STRAND' is from 2014
 */
@XmlEnum
@XmlType(name = "scheduleEventTypeEnum")
@Deprecated
public enum ScheduleEventType {

    STRAND;

    public String value() {
        return name();
    }

    public ScheduleEventType fromValue(String v) {
        return valueOf(v);
    }

}
