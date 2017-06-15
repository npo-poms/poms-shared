/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "media")
public class MediaResultList extends TransferList<MediaResultView> {

    private static final Logger LOG = LoggerFactory.getLogger(MediaResultList.class);


    private MediaResultList() {
    }

    public static MediaResultList create(MediaPermissionEvaluator permissionEvaluator, List<MediaListItem> fullList) {
        return create(permissionEvaluator, fullList, fullList.size());
    }

    public static MediaResultList create(MediaPermissionEvaluator permissionEvaluator, List<MediaListItem> fullList, Integer results) {
        MediaResultList simpleList = new MediaResultList();

        for(MediaListItem fullMedia : fullList) {
            try {
                simpleList.list.add(MediaResultView.create(permissionEvaluator, fullMedia));
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }

        simpleList.success = true;
        simpleList.results = results;

        return simpleList;
    }
}
