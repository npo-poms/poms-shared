/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "media")
public class MediaEditList extends TransferList<MediaEditView> {

    private MediaEditList() {
    }

    public static MediaEditList create(MediaPermissionEvaluator permissionEvaluator, MediaObject media, long episodeCount, long memberCount, boolean writable, Collection<Broadcaster> allBroadcasters, Collection<Portal> allowedPortals, boolean mayEditGenre) {
        MediaEditList simpleList = new MediaEditList();
        if(media != null) {
            simpleList.list.add(MediaEditView.create(permissionEvaluator, media, episodeCount, memberCount, writable, allBroadcasters, allowedPortals, mayEditGenre));
            simpleList.success = true;
            simpleList.results = 1;
        } else {
            simpleList.results = 0;
        }
        return simpleList;
    }
}
