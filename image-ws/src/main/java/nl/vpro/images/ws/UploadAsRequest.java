/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.images.ws;

import lombok.Getter;
import lombok.Setter;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.ImageType;

@XmlRootElement(name = "uploadAsRequest", namespace = Xmlns.IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadAsRequestType", propOrder = { "principalId" })
@Getter
@Setter
public class UploadAsRequest extends UploadRequest {

    @XmlElement(namespace = Xmlns.IMAGE_WS_NAMESPACE, required = true)
    private String principalId;

    public UploadAsRequest() {
        // nothing
    }

    public UploadAsRequest(String principalId, String title, String description, ImageType type, Boolean imageMetaData, DataHandler data) {
        super(title, description, type, imageMetaData, data);
        this.principalId = principalId;
    }

}
