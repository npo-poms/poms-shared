/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.MediaObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "locationFormType", propOrder = {
        "mediaObject",
        "pager"
        })
public class LocationForm {

    private MediaObject mediaObject;

    private LocationPager pager;

    private LocationForm() {}

    public LocationForm(LocationPager pager) {
        this.pager = pager;
    }

    public MediaObject getMediaObject() {
        return mediaObject;
    }

    public void setMediaObject(MediaObject mediaObject) {
        this.mediaObject = mediaObject;
    }

    public LocationPager getPager() {
        return pager;
    }

    public void setPager(LocationPager pager) {
        this.pager = pager;
    }
}
