/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import nl.vpro.domain.image.ImageMetadata;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "downloadResponse", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadResponseType", propOrder = {
    "urn",
    "imageMetadata"
})
public class DownloadResponse {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String urn;

    private ImageMetadata imageMetadata;


    public DownloadResponse() {
    }

    public DownloadResponse(String urn) {
        this.urn = urn;
    }

    public DownloadResponse(String urn, ImageMetadata imageMetadata) {
        this.urn = urn;
        this.imageMetadata = imageMetadata;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public ImageMetadata getImageMetadata() {
        return imageMetadata;
    }

    public void setImageMetadata(ImageMetadata imageMetadata) {
        this.imageMetadata = imageMetadata;
    }

    @Override
    public String toString() {
        return "DownloadResponse{" +
            "urn='" + urn + "\', imageMetadata='" + imageMetadata.toString() + "\'" + '}';
    }
}
