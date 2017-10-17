/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.image.ImageType;

@XmlRootElement(name = "downloadAsRequest", namespace = Xmlns.IMAGE_WS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadAsRequestType", propOrder = { "principalId" })
@Getter
@Setter
public class DownloadAsRequest extends DownloadRequest {

    @XmlElement(namespace = Xmlns.IMAGE_WS_NAMESPACE, required = true)
    private String principalId;

    public DownloadAsRequest() {
        // nothing
    }

    public DownloadAsRequest(String principalId, String title, String description, ImageType type, Boolean imageMetaData, String url) {
        super(title, description, type, imageMetaData, url);
        this.principalId = principalId;
    }

}
