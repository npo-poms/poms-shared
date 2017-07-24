/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.BasicImageMetadata;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@XmlRootElement(name = "downloadResponse", namespace = IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadResponseType", propOrder = {
    "urn",
    "imageMetadata"
})
public class DownloadResponse {

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String urn;

    private BasicImageMetadata imageMetadata;


    public DownloadResponse() {
    }

    public DownloadResponse(String urn) {
        this.urn = urn;
    }

    public DownloadResponse(String urn, BasicImageMetadata imageMetadata) {
        this.urn = urn;
        this.imageMetadata = imageMetadata;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public BasicImageMetadata getImageMetadata() {
        return imageMetadata;
    }

    public void setImageMetadata(BasicImageMetadata imageMetadata) {
        this.imageMetadata = imageMetadata;
    }

    @Override
    public String toString() {
        return "DownloadResponse{" +
            "urn='" + urn + "\', imageMetadata='" + imageMetadata.toString() + "\'" + '}';
    }
}
