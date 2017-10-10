/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@XmlRootElement(name = "downloadRequest", namespace = IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadRequestType", propOrder = {
    "title",
    "description",
    "type",
    "url",
    "imageMetadata"
})

@Setter
@Getter
public class DownloadRequest {

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String title;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String description;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private ImageType type;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String url;


    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private Boolean imageMetadata;


    public DownloadRequest() {
        // nothing
    }

    public DownloadRequest(String title, String description, ImageType type, String url) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
    }


}
