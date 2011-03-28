/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;

@XmlRootElement(name = "download", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadType", propOrder = {
        "title",
        "description",
        "type",
        "url",
        "principalId"})
public class Download {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String title;

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String description;

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private ImageType type;

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String url;

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String principalId;

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

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
}
