/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.images.ws;

import lombok.Getter;

import java.io.Serial;

import jakarta.xml.ws.WebFault;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@WebFault(name = "downloadFault", targetNamespace = IMAGE_WS_NAMESPACE)
public class DownloadFault extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6118553637207384235L;

    @Getter
    private final ImageNotFound faultInfo;

    public DownloadFault(String message) {
        this(message, new ImageNotFound(message));
    }

    public DownloadFault(String message, Throwable cause) {
        this(message, new ImageNotFound(message), cause);
    }

    public DownloadFault(String message, ImageNotFound faultInfo) { // public for CXF
        super(message);
        this.faultInfo = faultInfo;
    }

    public DownloadFault(String message, ImageNotFound faultInfo, Throwable cause) { // public for CXF
        super(message, cause);
        this.faultInfo = faultInfo;
    }

}
