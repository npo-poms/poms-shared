/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaObjects;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ownerdata", propOrder = {
        "crids"
        })
public class OwnerDataView extends MediaView {

    private List<String> crids = new ArrayList<String>();

    protected OwnerDataView() {
    }

    public static OwnerDataView create(MediaObject fullMedia, OwnerType owner) {
        OwnerDataView view = new OwnerDataView();
        view.title = MediaObjects.getTitle(fullMedia, owner, TextualType.MAIN);

        view.subTitle = MediaObjects.getTitle(fullMedia, owner, TextualType.SUB);
        if(StringUtils.isBlank(view.subTitle)) {
            view.subTitle = MediaObjects.getTitle(fullMedia, owner, TextualType.EPISODE);
        }

        view.shortTitle = MediaObjects.getTitle(fullMedia, owner, TextualType.SHORT);
        view.originalTitle= MediaObjects.getTitle(fullMedia, owner, TextualType.ORIGINAL);
        view.workTitle= MediaObjects.getTitle(fullMedia, owner, TextualType.WORK);

        view.description= MediaObjects.getDescription(fullMedia, owner, TextualType.MAIN);
        view.shortDescription = MediaObjects.getDescription(fullMedia, owner, TextualType.SHORT);

        for(String crid : fullMedia.getCrids()) {
            view.crids.add(crid);
        }
        return view;
    }

    public String[][] getCrids() {
        List<String[]> result = new ArrayList<String[]>(crids.size());
        for(String crid : crids) {
            result.add(new String[]{crid});
        }
        return result.toArray(new String[crids.size()][1]);
    }
}
