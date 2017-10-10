/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import lombok.Getter;
import lombok.Setter;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@XmlRootElement(name = "uploadRequest", namespace = IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "uploadRequestType", propOrder = {
        "title",
        "description",
        "type",
        "data"})
@Setter
@Getter
public class UploadRequest {

    @XmlElement(name = "title", namespace = IMAGE_WS_NAMESPACE, required = true)
    private String title;

    @XmlElement(name = "description", namespace = IMAGE_WS_NAMESPACE, required = true)
    private String description;

    @XmlElement(name = "type", namespace = IMAGE_WS_NAMESPACE, required = true)
    private ImageType type;

    @XmlElement(name = "data", namespace = IMAGE_WS_NAMESPACE, required = true)
    @XmlMimeType("application/octet-stream")
    private DataHandler data;

    public UploadRequest() {
    }

    public UploadRequest(String title, String description, ImageType type, DataHandler data) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.data = data;
    }

}
