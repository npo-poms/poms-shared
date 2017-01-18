/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;

@XmlRootElement(name = "uploadRequest", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "uploadRequestType", propOrder = {
        "title",
        "description",
        "type",
        "data"})
public class UploadRequest {

    private String title;

    private String description;

    private ImageType type;

    private DataHandler data;

    public UploadRequest() {
    }

    public UploadRequest(String title, String description, ImageType type, DataHandler data) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.data = data;
    }

    @XmlElement(name = "title", namespace = "urn:vpro:ws:image:2009", required = true)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "description", namespace = "urn:vpro:ws:image:2009", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "type", namespace = "urn:vpro:ws:image:2009", required = true)
    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    // the set ContentType from the client is not communicated, see also:
    // https://issues.jboss.org/browse/JBWS-3074
    @XmlElement(name = "data", namespace = "urn:vpro:ws:image:2009", required = true)
    @XmlMimeType("application/octet-stream")
    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler data) {
        this.data = data;
    }
}
