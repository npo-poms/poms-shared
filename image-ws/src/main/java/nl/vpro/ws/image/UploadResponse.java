/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.BasicImageMetadata;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@XmlRootElement(name = "uploadResponse", namespace = IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "uploadResponseType", propOrder = {
    "urn",
    "imageMetadata"
})
@Getter
@Setter
@ToString
public class UploadResponse {

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
    private String urn;

    @XmlElement(namespace = IMAGE_WS_NAMESPACE, required = true)
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


}
