/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import nl.vpro.domain.image.ImageMetadata;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "uploadResponse", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadResponseType", propOrder = {
    "urn",
    "imageMetadata"
})
public class UploadResponse {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String urn;

    private ImageMetadata imageMetadata;

    public UploadResponse() {
    }

    public UploadResponse(String urn) {
        this.urn = urn;
    }

    public UploadResponse(String urn, ImageMetadata imageMetadata) {
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
        return "UploadResponse{" +
            "urn='" + urn + "\', imageMetadata='" + imageMetadata.toString() + "\'" + '}';
    }
}
