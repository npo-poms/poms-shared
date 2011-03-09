/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "uploadResponse", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadResponseType", propOrder = {
        "urn"})
public class UploadResponse {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String urn;

    public UploadResponse() {
    }

    public UploadResponse(String urn) {
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
        return "UploadResponse{" +
            "urn='" + urn + '\'' +
            '}';
    }
}
