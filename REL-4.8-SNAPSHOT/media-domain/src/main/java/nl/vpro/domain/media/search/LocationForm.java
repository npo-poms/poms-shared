/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import nl.vpro.domain.media.MediaObject;

import static nl.vpro.domain.Xmlns.SEARCH_NAMESPACE;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationFormType", propOrder = {
        "mediaObject",
        "pager"
        })
public class LocationForm {

    private MediaObject mediaObject;

    private Pager pager;

    private LocationForm() {}

    public LocationForm(Pager pager) {
        this.pager = pager;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }
}
