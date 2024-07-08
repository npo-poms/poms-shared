/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import nl.vpro.validation.URI;

@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageLocationType", propOrder = {
    "mimeType",
    "url"
})
public class ImageLocation {


    @XmlElement
    @Getter
    private String mimeType;


    @XmlElement
    @NotNull(message = "provide image location")
    @URI(message = "provide a valid url to image location")
    @Getter
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
