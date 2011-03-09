/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "downloadResponse", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadResponseType", propOrder = {
        "urn"})
public class DownloadResponse {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String urn;

    public DownloadResponse() {
    }

    public DownloadResponse(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    @Override
    public String toString() {
        return "DownloadResponse{" +
            "urn='" + urn + '\'' +
            '}';
    }
}
