/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.MediaType;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "formats")
public class MediaTypesList extends TransferList<MediaTypeView> {

    private MediaTypesList() {
    }

    public static MediaTypesList create(MediaPermissionEvaluator permissionEvaluator) {
        MediaTypesList mediaTypesList = new MediaTypesList();

        for(MediaType type : MediaType.values()) {
            mediaTypesList.add(MediaTypeView.create(permissionEvaluator, type));
        }

        mediaTypesList.success = true;
        mediaTypesList.results = MediaType.values().length;

        return mediaTypesList;
    }
}
