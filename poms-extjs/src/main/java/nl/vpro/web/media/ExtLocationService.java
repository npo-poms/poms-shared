/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.web.media;

import nl.vpro.domain.media.search.LocationForm;
import nl.vpro.transfer.extjs.TransferList;

public interface ExtLocationService {

    TransferList findOrphans(LocationForm form);

}
