/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.transfer.extjs.TransferList;

public class MisDataList extends TransferList<MisDataView> {

    private MisDataList() {
    }

    public static MisDataList create(MediaObject mediaObject) {
        MisDataList list = new MisDataList();

        list.success = true;
        list.add(MisDataView.create(mediaObject));

        return list;
    }
}
