/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.web.media;

import java.util.Date;

import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.search.ScheduleForm;
import nl.vpro.transfer.extjs.TransferList;

public interface ExtScheduleService {
    TransferList<?> search(ScheduleForm form);

    TransferList<?> getSchedule(Channel channel, Date day);

}
