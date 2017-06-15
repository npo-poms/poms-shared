/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "ownerData")
public class OwnerDataList extends TransferList<OwnerDataView> {

    private OwnerDataList() {
    }

    public static OwnerDataList create(MediaObject media, OwnerType owner) {
        OwnerDataList simpleList = new OwnerDataList();
        if(media != null) {
            simpleList.list.add(OwnerDataView.create(media, owner));
            simpleList.success = true;
            simpleList.results = 1;
        } else {
            simpleList.results = 0;
        }
        return simpleList;
    }
}
