/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import lombok.Getter;
import lombok.Setter;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.*;

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

    public UploadAsRequest(String principalId, String title, String description, ImageType type, DataHandler data) {
        super(title, description, type, data);
        this.principalId = principalId;
    }

}
