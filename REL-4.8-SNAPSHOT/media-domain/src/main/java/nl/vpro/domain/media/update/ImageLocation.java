/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.hibernate.validator.constraints.URL;

import nl.vpro.domain.Xmlns;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageLocationType", propOrder = {
        "url"
        })
public class ImageLocation {

    @XmlElement
    @NotNull(message = "provide image location")
    @URL(message = "provide a valid url to image location")
    private String url;

    private ImageLocation() {

    }

    public ImageLocation(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ImageLocation");
        sb.append("{url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
