/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.validation.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageLocationType", propOrder = {
        "url"
        })
public class ImageLocation {

    @XmlElement
    @NotNull(message = "provide image location")
    @URI(message = "provide a valid url to image location")
    private String url;

    private ImageLocation() {

    }

    public ImageLocation(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImageLocation" +
            "{url='" + url + '\'' +
            '}';
    }
}
