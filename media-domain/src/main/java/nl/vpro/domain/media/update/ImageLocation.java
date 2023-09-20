/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.validation.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageLocationType", propOrder = {
    "mimeType",
    "url"
})
public class ImageLocation {


    @XmlElement
    @Getter
    @Setter
    private String mimeType;


    @XmlElement
    @NotNull(message = "provide image location")
    @URI(message = "provide a valid url to image location")
    @Getter
    @Setter
    private String url;


    public ImageLocation() {

    }

    public ImageLocation(String url) {
        this.url = url;
    }



    @lombok.Builder
    private ImageLocation(String mimeType, String url) {
        this.url = url;
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "ImageLocation" +
            "{url='" + url + '\'' +
            '}';
    }
}
