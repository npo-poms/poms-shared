/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import lombok.Getter;

import javax.xml.ws.WebFault;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@WebFault(name = "downloadFault", targetNamespace = IMAGE_WS_NAMESPACE)
public class DownloadFault extends RuntimeException {

    @Getter
    private final ImageNotFound faultInfo;

    public DownloadFault(String message) {
        this(message, new ImageNotFound(message));
    }

    public DownloadFault(String message, Throwable cause) {
        this(message, new ImageNotFound(message), cause);
    }

    private DownloadFault(String message, ImageNotFound faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    private DownloadFault(String message, ImageNotFound faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

}
