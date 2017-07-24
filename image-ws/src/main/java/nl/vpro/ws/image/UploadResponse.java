/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.BasicImageMetadata;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@XmlRootElement(name = "uploadResponse", namespace = IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadResponseType", propOrder = {
    "urn",
    "imageMetadata"
})
public class UploadResponse {

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String urn;

    private BasicImageMetadata imageMetadata;

    public UploadResponse() {
    }

    public UploadResponse(String urn) {
        this.urn = urn;
    }

    public UploadResponse(String urn, BasicImageMetadata imageMetadata) {
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
        return "UploadResponse{" +
            "urn='" + urn + "\', imageMetadata='" + imageMetadata.toString() + "\'" + '}';
    }
}
