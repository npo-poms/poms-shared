/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "upload", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadType", propOrder = {
        "title",
        "description",
        "data"})
public class Upload {
    @XmlElement(name = "title", namespace = "urn:vpro:ws:image:2009", required = true)
    private String title;

    @XmlElement(name = "description", namespace = "urn:vpro:ws:image:2009", required = true)
    private String description;

    @XmlElement(name = "data", namespace = "urn:vpro:ws:image:2009", required = true)
    @XmlMimeType("application/octet-stream")
    private DataHandler data;

    public Upload() {
    }

    public Upload(String title, String description, DataHandler data) {
        this.title = title;
        this.description = description;
        this.data = data;
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

    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler data) {
        this.data = data;
    }
}
