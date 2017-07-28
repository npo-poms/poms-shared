/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@XmlRootElement(name = "downloadRequest", namespace = IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadRequestType", propOrder = {
        "title",
        "description",
        "type",
        "url"})
public class DownloadRequest {

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String title;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String description;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private ImageType type;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String url;


    public DownloadRequest() {
        // nothing
    }

    public DownloadRequest(String title, String description, ImageType type, String url) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
