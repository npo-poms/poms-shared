/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;

@XmlRootElement(name = "downloadAsRequest", namespace = "urn:vpro:ws:image:2009")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadAsRequestType", propOrder = {
		"principalId"})
public class DownloadAsRequest extends DownloadRequest {

    @XmlElement(namespace = "urn:vpro:ws:image:2009", required = true)
    private String principalId;

    public DownloadAsRequest() {
    	// nothing
    }

 	public DownloadAsRequest(String principalId, String title, String description, ImageType type,
			String url) {
		super(title, description, type, url);
		this.principalId = principalId;
	}

	public String getPrincipalId() {
    	return principalId;
    }

    public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}

}
