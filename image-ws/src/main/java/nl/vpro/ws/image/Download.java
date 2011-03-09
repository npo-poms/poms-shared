/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "download", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadType", propOrder = {
        "url"})
public class Download {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String url;

    public Download() {
    }

    public Download(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
