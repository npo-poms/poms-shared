/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.web.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.vpro.domain.media.LocationService;
import nl.vpro.domain.media.search.LocationForm;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.media.LocationList;


@Service("extLocationService")
@Transactional
public class ExtLocationServiceImpl implements ExtLocationService {

    @Autowired
    private LocationService locationService;

    @Override
    public TransferList findOrphans(LocationForm form) {
        return LocationList.create(locationService.find(form));
    }
}
